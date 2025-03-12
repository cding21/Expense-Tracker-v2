export interface Transaction {
    userId: string;
    date: string;
    amount: number;
    description: string;
    category: string;
    fromAccount: string;
    fromNote: string;
    toAccount: string;
    toNote: string;
}