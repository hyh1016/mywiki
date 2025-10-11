import React, {useEffect, useRef, useState} from 'react';
import {useBlocker, useNavigate, useParams, useSearchParams} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import {apiClient} from '../api/apiClient';
import {useSummaryForm} from '../hooks/useSummaryForm';
import {TemplateSection} from '../types/summary';
import './SummaryFormPage.css';

const SummaryFormPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const bookmarkId = searchParams.get('bookmarkId');

    const {
        isEditMode,
        templates,
        selectedSubSections,
        contents,
        isLoading,
        error,
        setContents,
        setSelectedSubSections,
    } = useSummaryForm(id);

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [isNavigating, setIsNavigating] = useState(false);
    const [navigationTarget, setNavigationTarget] = useState<string | null>(null);
    const textAreaRefs = useRef<{ [key: number]: HTMLTextAreaElement | null }>({});

    const isDirty = Object.values(contents).some(content => content && content.length > 0);
    const blocker = useBlocker(isDirty && !isNavigating);

    useEffect(() => {
        if (navigationTarget) {
            navigate(navigationTarget);
        }
    }, [navigationTarget, navigate]);

    useEffect(() => {
        const handleBeforeUnload = (e: BeforeUnloadEvent) => {
            e.preventDefault();
            e.returnValue = '';
        };
        if (isDirty && !isNavigating) window.addEventListener('beforeunload', handleBeforeUnload);
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, [isDirty, isNavigating]);

    useEffect(() => {
        if (blocker.state === 'blocked') {
            if (window.confirm('이 페이지를 벗어나면 변경 내용이 사라집니다. 정말 벗어나시겠습니까?')) {
                blocker.proceed();
            } else {
                blocker.reset();
            }
        }
    }, [blocker]);

    const handleContentChange = (subSectionId: number, value: string) => {
        setContents(prev => ({ ...prev, [subSectionId]: value }));
    };

    const handleSelectChange = (sectionTitle: string, subSectionId: number) => {
        setSelectedSubSections(prev => ({ ...prev, [sectionTitle]: subSectionId }));
    };

    const handleMultiSelectChange = (sectionTitle: string, subSectionId: number) => {
        const currentSelection = selectedSubSections[sectionTitle];
        const currentIds = Array.isArray(currentSelection) ? currentSelection : [];
        const isUnchecking = currentIds.includes(subSectionId);

        if (isUnchecking && contents[subSectionId]) {
            if (!window.confirm('해당 세션을 지우면 작성한 내용이 사라집니다. 정말 삭제하시겠습니까?')) {
                return;
            }
        }

        const newIds = isUnchecking
            ? currentIds.filter(id => id !== subSectionId)
            : [...currentIds, subSectionId];

        if (isUnchecking) {
            setContents(prev => {
                const newContents = { ...prev };
                delete newContents[subSectionId];
                return newContents;
            });
        }
        setSelectedSubSections(prev => ({ ...prev, [sectionTitle]: newIds }));
    };

    const validateForm = (): boolean => {
        const coreInsightSection = templates['핵심 파악'];
        if (coreInsightSection) {
            const subSectionId = coreInsightSection.element[0].id;
            if (!contents[subSectionId] || contents[subSectionId].trim() === '') {
                alert("'핵심 파악' 섹션은 필수 입력 항목입니다.");
                textAreaRefs.current[subSectionId]?.focus();
                return false;
            }
        }

        const detailsSection = templates['세부 내용 정리'];
        if (detailsSection) {
            const selectedSubSectionId = selectedSubSections['세부 내용 정리'];
            if (!selectedSubSectionId) {
                alert("'세부 내용 정리'에서 템플릿을 선택해주세요.");
                return false;
            }
            if (!contents[selectedSubSectionId as number] || contents[selectedSubSectionId as number].trim() === '') {
                alert("'세부 내용 정리' 섹션은 필수 입력 항목입니다.");
                textAreaRefs.current[selectedSubSectionId as number]?.focus();
                return false;
            }
        }
        return true;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;
        if (!window.confirm(isEditMode ? '수정하시겠습니까?' : '저장하시겠습니까?')) return;
        if (!isEditMode && !bookmarkId) {
            setSubmitError('북마크 정보가 없습니다.');
            return;
        }

        setIsSubmitting(true);
        setSubmitError(null);

        const contentsArray = Object.entries(contents)
            .filter(([, content]) => content?.trim() !== '')
            .map(([id, content]) => ({
                id: parseInt(id, 10),
                content: content
            }));

        const payload = isEditMode
            ? { contents: contentsArray }
            : { bookmarkId: parseInt(bookmarkId!, 10), contents: contentsArray };

        try {
            if (isEditMode) {
                await apiClient.put(`/api/summaries/${id}`, payload);
                alert('수정되었습니다');
                setIsNavigating(true);
                setNavigationTarget(`/summaries/${id}`);
            } else {
                const response = await apiClient.post('/api/summaries', payload);
                alert('저장되었습니다');
                const newId = response.data.id; // Use ID from response body
                setIsNavigating(true);
                setNavigationTarget(`/summaries/${newId}`);
            }
        } catch (err) {
            setSubmitError(isEditMode ? '요약 수정에 실패했습니다.' : '요약 저장에 실패했습니다.');
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
        return <Layout title={isEditMode ? "요약 수정하기" : "요약 작성하기"}><div className="add-summary-page-content"><p>로딩 중...</p></div></Layout>;
    }

    return (
        <Layout title={isEditMode ? "요약 수정하기" : "요약 작성하기"}>
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
                    {submitError && <p className="error-message">{submitError}</p>}
                    <Button onClick={handleSubmit} disabled={isSubmitting} className="submit-btn">
                        {isSubmitting ? (isEditMode ? '수정 중...' : '저장 중...') : (isEditMode ? '요약 수정하기' : '요약 저장하기')}
                    </Button>
                </div>
            </div>
        </Layout>
    );
};

export default SummaryFormPage;
