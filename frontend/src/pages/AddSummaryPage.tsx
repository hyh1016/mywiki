import React, {useEffect, useRef, useState} from 'react';
import {useBlocker, useNavigate, useSearchParams} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import {apiClient} from '../api/apiClient';
import './AddSummaryPage.css';

// API 응답 데이터 타입 정의
interface SummaryTemplateElement {
    id: number;
    section: string;
    title: string;
    description: string | null;
}

interface TemplateSection {
    order: number;
    type: 'STATIC' | 'SELECT' | 'MULTI_SELECT';
    element: SummaryTemplateElement[];
}

interface TemplatesResponse {
    templates: Record<string, TemplateSection>;
}

interface SubmissionResponse {
    summaryId: number;
}

// 컴포넌트 내부 상태 타입
interface SelectedSubSections {
    [sectionTitle: string]: number | number[] | null;
}

interface Contents {
    [subSectionId: number]: string;
}

const AddSummaryPage: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const bookmarkId = searchParams.get('bookmarkId');

    const [templates, setTemplates] = useState<Record<string, TemplateSection>>({});
    const [selectedSubSections, setSelectedSubSections] = useState<SelectedSubSections>({});
    const [contents, setContents] = useState<Contents>({});
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const textAreaRefs = useRef<{ [key: number]: HTMLTextAreaElement | null }>({});

    const isDirty = Object.values(contents).some(content => content && content.length > 0);
    const blocker = useBlocker(isDirty);

    // 브라우저 이탈 방지 (새로고침, 탭 닫기 등)
    useEffect(() => {
        const handleBeforeUnload = (e: BeforeUnloadEvent) => {
            e.preventDefault();
            e.returnValue = ''; // Chrome requires returnValue to be set.
        };

        if (isDirty) {
            window.addEventListener('beforeunload', handleBeforeUnload);
        }

        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
        };
    }, [isDirty]);

    // 앱 내부 라우팅 이탈 방지 (뒤로가기, 링크 클릭 등)
    useEffect(() => {
        if (blocker.state === 'blocked') {
            if (window.confirm('이 페이지를 벗어나면 변경 내용이 사라집니다. 정말 벗어나시겠습니까?')) {
                blocker.proceed();
            } else {
                blocker.reset();
            }
        }
    }, [blocker]);

    useEffect(() => {
        const fetchTemplates = async () => {
            try {
                const response = await apiClient.get<TemplatesResponse>('/api/summary-templates');
                const fetchedTemplates = response.data.templates;
                setTemplates(fetchedTemplates);

                const initialSelections: SelectedSubSections = {};
                Object.entries(fetchedTemplates).forEach(([title, section]) => {
                    if (section.type === 'STATIC') {
                        initialSelections[title] = section.element[0].id;
                    } else {
                        initialSelections[title] = null;
                    }
                });
                setSelectedSubSections(initialSelections);

            } catch (err) {
                setError('요약 템플릿을 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchTemplates();
    }, []);

    const handleSelectChange = (sectionTitle: string, subSectionId: number) => {
        setSelectedSubSections(prev => ({...prev, [sectionTitle]: subSectionId}));
    };

    const handleMultiSelectChange = (sectionTitle: string, subSectionId: number) => {
        const currentSelection = selectedSubSections[sectionTitle];
        const currentIds = Array.isArray(currentSelection) ? currentSelection : [];
        const isUnchecking = currentIds.includes(subSectionId);

        if (isUnchecking && contents[subSectionId]) {
            if (!window.confirm('해당 세션을 지우면 작성한 내용이 사라집니다. 정말 삭제하시겠습니까?')) {
                return; // 취소 시 중단
            }
        }

        const newIds = isUnchecking
            ? currentIds.filter(id => id !== subSectionId)
            : [...currentIds, subSectionId];

        if (isUnchecking) {
            setContents(prev => {
                const newContents = {...prev};
                delete newContents[subSectionId];
                return newContents;
            });
        }

        setSelectedSubSections(prev => ({...prev, [sectionTitle]: newIds}));
    };

    const handleContentChange = (subSectionId: number, value: string) => {
        setContents(prev => ({...prev, [subSectionId]: value}));
    };

    const handleSubmit = async () => {
        // 유효성 검사 먼저 실행
        const coreInsightSection = templates['핵심 파악'];
        if (coreInsightSection) {
            const subSectionId = coreInsightSection.element[0].id;
            if (!contents[subSectionId] || contents[subSectionId].trim() === '') {
                alert("'핵심 파악' 섹션은 필수 입력 항목입니다.");
                textAreaRefs.current[subSectionId]?.focus();
                return;
            }
        }

        const detailsSection = templates['세부 내용 정리'];
        if (detailsSection) {
            const selectedSubSectionId = selectedSubSections['세부 내용 정리'];
            if (!selectedSubSectionId) {
                alert("'세부 내용 정리'에서 템플릿을 선택해주세요.");
                return;
            }
            if (!contents[selectedSubSectionId as number] || contents[selectedSubSectionId as number].trim() === '') {
                alert("'세부 내용 정리' 섹션은 필수 입력 항목입니다.");
                textAreaRefs.current[selectedSubSectionId as number]?.focus();
                return;
            }
        }

        // 유효성 검사 통과 후 저장 확인
        if (!window.confirm('저장하시겠습니까?')) {
            return;
        }

        if (!bookmarkId) {
            setError('북마크 정보가 없습니다.');
            return;
        }
        setIsSubmitting(true);
        setError(null);

        let markdownContent = '';
        Object.entries(templates)
            .sort(([, a], [, b]) => a.order - b.order)
            .forEach(([title, section]) => {
                const selection = selectedSubSections[title];
                if (!selection || (Array.isArray(selection) && selection.length === 0)) return;

                markdownContent += `## ${title}\n\n`;

                const elementMap = new Map(section.element.map(el => [el.id, el]));

                if (Array.isArray(selection)) { // MULTI_SELECT
                    selection.forEach(id => {
                        const subSection = elementMap.get(id);
                        if (subSection) {
                            markdownContent += `### ${subSection.title}\n`;
                            markdownContent += `${contents[id] || ''}\n\n`;
                        }
                    });
                } else { // STATIC or SELECT
                    const subSection = elementMap.get(selection as number);
                    if (subSection) {
                        markdownContent += `### ${subSection.title}\n`;
                        markdownContent += `${contents[selection as number] || ''}\n\n`;
                    }
                }
            });

        try {
            const response = await apiClient.post<SubmissionResponse>('/api/summaries', {
                bookmarkId: parseInt(bookmarkId, 10),
                content: markdownContent.trim(),
            });
            alert('저장되었습니다');
            navigate(`/summaries/${response.data.summaryId}`);
        } catch (err) {
            setError('요약 저장에 실패했습니다. 네트워크 연결을 확인하고 다시 시도해주세요.');
            console.error(err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderSection = (title: string, section: TemplateSection) => {
        const selection = selectedSubSections[title];

        switch (section.type) {
            case 'STATIC':
                const staticEl = section.element[0];
                return (
                    <div key={staticEl.id} className="sub-section">
                        <h3>{staticEl.title}</h3>
                        {staticEl.description && <p className="description">{staticEl.description}</p>}
                        <textarea
                            ref={(el) => { textAreaRefs.current[staticEl.id] = el; }}
                            value={contents[staticEl.id] || ''}
                            onChange={(e) => handleContentChange(staticEl.id, e.target.value)}
                            placeholder="내용을 입력하세요..."
                        />
                    </div>
                );

            case 'SELECT':
                const selectedSubSection = section.element.find(el => el.id === selection);
                return (
                    <>
                        <select className="section-select" value={selection as number || ''} onChange={(e) => handleSelectChange(title, parseInt(e.target.value))}>
                            <option value="" disabled>템플릿 선택</option>
                            {section.element.map(el => (
                                <option key={el.id} value={el.id}>{el.title}</option>
                            ))}
                        </select>
                        {selectedSubSection && (
                            <div key={selectedSubSection.id} className="sub-section">
                                <h3>{selectedSubSection.title}</h3>
                                <textarea
                                    ref={(el) => { textAreaRefs.current[selectedSubSection.id] = el; }}
                                    value={contents[selectedSubSection.id] || ''}
                                    onChange={(e) => handleContentChange(selectedSubSection.id, e.target.value)}
                                    placeholder="내용을 입력하세요..."
                                />
                            </div>
                        )}
                    </>
                );

            case 'MULTI_SELECT':
                const selectedIds = (Array.isArray(selection) ? selection : []) as number[];
                return (
                    <>
                        <div className="multi-select-container">
                            {section.element.map(el => (
                                <label key={el.id} className="multi-select-label">
                                    <input
                                        type="checkbox"
                                        checked={selectedIds.includes(el.id)}
                                        onChange={() => handleMultiSelectChange(title, el.id)}
                                    />
                                    {el.title}
                                </label>
                            ))}
                        </div>
                        {selectedIds.map(id => {
                            const subSection = section.element.find(el => el.id === id);
                            if (!subSection) return null;
                            return (
                                <div key={id} className="sub-section removable">
                                    <div className="sub-section-header">
                                        <h3>{subSection.title}</h3>
                                        <Button onClick={() => handleMultiSelectChange(title, id)} className="remove-btn">×</Button>
                                    </div>
                                    <textarea
                                        ref={(el) => { textAreaRefs.current[id] = el; }}
                                        value={contents[id] || ''}
                                        onChange={(e) => handleContentChange(id, e.target.value)}
                                        placeholder="내용을 입력하세요..."
                                    />
                                </div>
                            );
                        })}
                    </>
                );
            default:
                return null;
        }
    };

    if (isLoading) {
        return <Layout title="요약 작성하기"><div className="add-summary-page-content"><p>로딩 중...</p></div></Layout>;
    }

    return (
        <Layout title="요약 작성하기">
            <div className="add-summary-page-content">
                <div className="summary-form">
                    {Object.entries(templates)
                        .sort(([, a], [, b]) => a.order - b.order)
                        .map(([title, section]) => (
                            <section key={section.order} className="summary-section">
                                <h2>{title}</h2>
                                {renderSection(title, section)}
                            </section>
                        ))}
                    {error && <p className="error-message">{error}</p>}
                    <Button onClick={handleSubmit} disabled={isSubmitting} className="submit-btn">
                        {isSubmitting ? '저장 중...' : '요약 저장하기'}
                    </Button>
                </div>
            </div>
        </Layout>
    );
};

export default AddSummaryPage;
