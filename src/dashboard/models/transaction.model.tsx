export type Transaction = {
    id: string;
    userId: string;
    date: string;
    amount: number;
    currencyCode: string;
    description: string;
    category: string;
    fromAccount: string;
    fromNote: string;
    toAccount: string;
    toNote: string;
}