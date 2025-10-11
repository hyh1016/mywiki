export interface BookmarkResponse {
    id: number;
    url: string;
    title: string;
    description: string | null;
    image: string | null;
    createdAt: string;
}

export interface SummaryTemplate {
    id: number;
    section: string;
    title: string;
    description: string | null;
}

export interface TemplateSection {
    order: number;
    type: 'STATIC' | 'SELECT' | 'MULTI_SELECT';
    element: SummaryTemplate[];
}

export interface TemplatesResponse {
    templates: Record<string, TemplateSection>;
}

export interface SummaryDetailContentItem {
    id: number;
    title: string;
    description: string | null;
    content: string;
}

export interface SummaryDetailSection {
    title: string;
    content: SummaryDetailContentItem[];
}

export type SummaryDetailResponse = Record<string, SummaryDetailSection>;

export interface SummaryResponse {
    id: number;
    bookmark: BookmarkResponse;
    contents: SummaryDetailResponse;
    createdAt: string;
    updatedAt: string;
}

// --- Component State Types ---
export interface SelectedSubSections {
    [sectionTitle: string]: number | number[] | null;
}

export interface Contents {
    [subSectionId: number]: string;
}
