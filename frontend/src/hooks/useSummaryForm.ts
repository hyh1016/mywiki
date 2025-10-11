import {useEffect, useState} from 'react';
import {apiClient} from '../api/apiClient';
import {Contents, SelectedSubSections, SummaryResponse, TemplatesResponse} from '../types/summary';

export const useSummaryForm = (id?: string) => {
    const isEditMode = !!id;
    const [templates, setTemplates] = useState<TemplatesResponse['templates']>({});
    const [selectedSubSections, setSelectedSubSections] = useState<SelectedSubSections>({});
    const [contents, setContents] = useState<Contents>({});
    const [bookmark, setBookmark] = useState<SummaryResponse['bookmark'] | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                const templateResponse = await apiClient.get<TemplatesResponse>('/api/summary-templates');
                const fetchedTemplates = templateResponse.data.templates;
                setTemplates(fetchedTemplates);

                if (isEditMode) {
                    const summaryResponse = await apiClient.get<SummaryResponse>(`/api/summaries/${id}`);
                    const summaryData = summaryResponse.data;
                    const summaryContents = summaryData.contents;
                    setBookmark(summaryData.bookmark);

                    const initialContents: Contents = {};
                    const initialSelections: SelectedSubSections = {};

                    Object.values(summaryContents).forEach(section => {
                        section.content.forEach(item => {
                            initialContents[item.id] = item.content;
                        });
                    });

                    Object.entries(fetchedTemplates).forEach(([title, sectionInfo]) => {
                        const summarySection = Object.values(summaryContents).find(s => s.title === title);
                        const summaryContentIds = summarySection ? summarySection.content.map(c => c.id) : [];

                        if (sectionInfo.type === 'STATIC' || sectionInfo.type === 'SELECT') {
                            initialSelections[title] = summaryContentIds[0] || null;
                        } else if (sectionInfo.type === 'MULTI_SELECT') {
                            initialSelections[title] = summaryContentIds;
                        }
                    });

                    setContents(initialContents);
                    setSelectedSubSections(initialSelections);
                } else {
                    const initialSelections: SelectedSubSections = {};
                    Object.entries(fetchedTemplates).forEach(([title, section]) => {
                        if (section.type === 'STATIC') {
                            initialSelections[title] = section.element[0].id;
                        } else {
                            initialSelections[title] = null;
                        }
                    });
                    setSelectedSubSections(initialSelections);
                }
            } catch (err) {
                setError('데이터를 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchInitialData();
    }, [id, isEditMode]);

    return {
        isEditMode,
        templates,
        selectedSubSections,
        contents,
        bookmark,
        isLoading,
        error,
        setContents,
        setSelectedSubSections,
    };
};
