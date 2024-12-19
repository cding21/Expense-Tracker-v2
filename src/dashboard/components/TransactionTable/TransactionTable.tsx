import { useMemo, useState } from 'react';
import {
  MantineReactTable,
  // createRow,
  type MRT_ColumnDef,
  type MRT_Row,
  type MRT_TableOptions,
  useMantineReactTable,
} from 'mantine-react-table';
import { ActionIcon, Button, Flex, Text, Tooltip } from '@mantine/core';
import { modals } from '@mantine/modals';
import { IconTrash } from '@tabler/icons-react';
import {
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query';
import { mockTransactionCategories, mockTransactionCurrencyCodes, mockTransactionList } from '@/mockTransaction';
import { Transaction } from '@/models/transaction.model';
import dayjs, { Dayjs } from 'dayjs';


const TransactionTable = () => {
  const [validationErrors, setValidationErrors] = useState<
    Record<string, string | undefined>
  >({});
  //keep track of rows that have been edited
  const [editedTransactions, setEditedTransactions] = useState<Record<string, Transaction>>({});

  //call CREATE hook
  const { mutateAsync: createTransaction, isPending: isCreatingTransaction } =
    useCreateTransaction();
  //call READ hook
  const {
    data: fetchedTransactions = [],
    isError: isLoadingTransactionsError,
    isFetching: isFetchingTransactions,
    isLoading: isLoadingTransactions,
  } = useGetTransactions();
  //call UPDATE hook
  const { mutateAsync: updateTransactions, isPending: isUpdatingTransaction } =
    useUpdateTransactions();
  //call DELETE hook
  const { mutateAsync: deleteTransaction, isPending: isDeletingTransaction } =
    useDeleteTrasanction();

  //CREATE action
  const handleCreateTransaction: MRT_TableOptions<Transaction>['onCreatingRowSave'] = async ({
    values,
    exitCreatingMode,
  }) => {
    const newValidationErrors = validateTransaction(values);
    if (Object.values(newValidationErrors).some((error) => !!error)) {
      setValidationErrors(newValidationErrors);
      return;
    }
    setValidationErrors({});
    await createTransaction(values);
    exitCreatingMode();
  };

  //UPDATE action
  const handleSaveTransactions = async () => {
    if (Object.values(validationErrors).some((error) => !!error)) return;
    await updateTransactions(Object.values(editedTransactions));
    setEditedTransactions({});
  };

  //DELETE action
  const openDeleteConfirmModal = (row: MRT_Row<Transaction>) =>
    modals.openConfirmModal({
      title: 'Are you sure you want to delete this transaction?',
      children: (
        <Text>
          Are you sure you want to delete {row.original.description}{' '}
          {row.original.amount}? This action cannot be undone.
        </Text>
      ),
      labels: { confirm: 'Delete', cancel: 'Cancel' },
      confirmProps: { color: 'red' },
      onConfirm: () => deleteTransaction(row.original.id),
    });

  const columns = useMemo<MRT_ColumnDef<Transaction>[]>(
    () => [
      {
        accessorKey: 'id',
        header: 'Id',
        enableEditing: false,
        size: 80,
      },
      {
        accessorFn: (row) => {
          console.log(row.date);
          return dayjs(row.date, 'DD/MM/YYYY');
        },
        header: 'Date',
        editVariant: 'text',
        filterVariant: 'date-range',
        Cell: ({ cell }) => cell.getValue<Dayjs>()?.toDate().toLocaleString(),
        mantineEditTextInputProps: ({ cell, row }) => ({
          type: 'date',
          required: true,
          error: validationErrors?.[cell.id],
          //store edited user in state to be saved later
          onBlur: (event) => {
            const validationError = !validateRequired(event.currentTarget.value)
              ? 'Required'
              : undefined;
            setValidationErrors({
              ...validationErrors,
              [cell.id]: validationError,
            });
            row.original.date = dayjs(event.currentTarget.value).format('DD/MM/YYYY');
            setEditedTransactions({ ...editedTransactions, [row.id]: {...row.original, date: dayjs(event.currentTarget.value).format('DD/MM/YYYY')} });
          },
        }),
      },
      {
        accessorKey: 'amount',
        header: 'Amount',
        filterVariant: 'range',
        mantineFilterRangeSliderProps: {
          color: 'indigo',
          label: (value) =>
            value?.toLocaleString?.('en', {
              style: 'currency',
              currency: 'AUD',
              minimumFractionDigits: 0,
              maximumFractionDigits: 0,
            }),
        },
        mantineEditTextInputProps: ({ cell, row }) => ({
          type: 'text',
          required: true,
          error: validationErrors?.[cell.id],
          //store edited user in state to be saved later
          onBlur: (event) => {
            const validationError = !validateRequired(event.currentTarget.value)
              ? 'Required'
              : undefined;
            setValidationErrors({
              ...validationErrors,
              [cell.id]: validationError,
            });
            setEditedTransactions({ ...editedTransactions, [row.id]: row.original });
          },
        }),
      },
      {
        accessorKey: 'currencyCode',
        header: 'Currency Code',
        editVariant: 'select',
        filterVariant: 'multi-select',
        mantineEditSelectProps: ({ row }) => ({
          data: mockTransactionCurrencyCodes,
          //store edited user in state to be saved later
          onChange: (value: any) =>
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, currencyCode: value },
            }),
        }),
      },
      {
        accessorKey: 'description',
        header: 'Description',
        mantineEditTextInputProps: ({ cell, row }) => ({
          type: 'text',
          required: true,
          error: validationErrors?.[cell.id],
          //store edited user in state to be saved later
          onBlur: (event) => {
            const validationError = !validateRequired(event.currentTarget.value)
              ? 'Required'
              : undefined;
            setValidationErrors({
              ...validationErrors,
              [cell.id]: validationError,
            });
            setEditedTransactions({ ...editedTransactions, [row.id]: row.original });
          },
        }),
      },
      {
        accessorKey: 'category',
        header: 'Category',
        editVariant: 'select',
        mantineEditSelectProps: ({ row }) => ({
          data: mockTransactionCategories,
          //store edited user in state to be saved later
          onChange: (value: any) =>
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, category: value },
            }),
        }),
      },
    ],
    [editedTransactions, validationErrors],
  );

  const table = useMantineReactTable({
    columns,
    data: fetchedTransactions,
    createDisplayMode: 'row', // ('modal', and 'custom' are also available)
    editDisplayMode: 'cell', // ('modal', 'row', 'table', and 'custom' are also available)
    enableColumnFilterModes: true,
    enableEditing: true,
    enableRowActions: true,
    enableRowSelection: true,
    positionActionsColumn: 'last',
    getRowId: (row) => row.id,
    mantineToolbarAlertBannerProps: isLoadingTransactionsError
      ? {
          color: 'red',
          children: 'Error loading data',
        }
      : undefined,
    mantineTableContainerProps: {
      style: {
        minHeight: '500px',
      },
    },
    mantineSearchTextInputProps: {
      placeholder: 'Search Transactions',
    },
    onCreatingRowCancel: () => setValidationErrors({}),
    onCreatingRowSave: handleCreateTransaction,
    renderRowActions: ({ row }) => (
      <Tooltip label="Delete">
        <ActionIcon color="red" onClick={() => openDeleteConfirmModal(row)}>
          <IconTrash />
        </ActionIcon>
      </Tooltip>
    ),
    renderBottomToolbarCustomActions: () => (
      <Flex align="center" gap="md">
        <Button
          color="blue"
          onClick={handleSaveTransactions}
          disabled={
            Object.keys(editedTransactions).length === 0 ||
            Object.values(validationErrors).some((error) => !!error)
          }
          loading={isUpdatingTransaction}
        >
          Save
        </Button>
        {Object.values(validationErrors).some((error) => !!error) && (
          <Text c="red">Fix errors before submitting</Text>
        )}
      </Flex>
    ),
    renderTopToolbarCustomActions: ({ table }) => (
      <Button
        onClick={() => {
          table.setCreatingRow(true); //simplest way to open the create row modal with no default values
          //or you can pass in a row object to set default values with the `createRow` helper function
          // table.setCreatingRow(
          //   createRow(table, {
          //     //optionally pass in default values for the new row, useful for nested data or other complex scenarios
          //   }),
          // );
        }}
      >
        Create New Transaction
      </Button>
    ),
    state: {
      isLoading: isLoadingTransactions,
      isSaving: isCreatingTransaction || isUpdatingTransaction || isDeletingTransaction,
      showAlertBanner: isLoadingTransactionsError,
      showProgressBars: isFetchingTransactions,
    },
  });

  return <MantineReactTable table={table} />;
};

//CREATE hook (post new user to api)
function useCreateTransaction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (trans: Transaction) => {
      //send api update request here
      await new Promise((resolve) => setTimeout(resolve, 1000)); //fake api call
      return Promise.resolve();
    },
    //client side optimistic update
    onMutate: (newTransInfo: Transaction) => {
      queryClient.setQueryData(
        ['Transactions'],
        (prevTransactions: any) =>
          [
            ...prevTransactions,
            {
              ...newTransInfo,
              id: (Math.random() + 1).toString(36).substring(7),
            },
          ] as Transaction[],
      );
    },
    // onSettled: () => queryClient.invalidateQueries({ queryKey: ['Transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

//READ hook (get Transactions from api)
function useGetTransactions() {
  return useQuery<Transaction[]>({
    queryKey: ['Transactions'],
    queryFn: async () => {
      //send api request here
      await new Promise((resolve) => setTimeout(resolve, 1000)); //fake api call
      return Promise.resolve(mockTransactionList);
    },
    refetchOnWindowFocus: false,
  });
}

//UPDATE hook (put Transactions in api)
function useUpdateTransactions() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (transactions: Transaction[]) => {
      //send api update request here
      await new Promise((resolve) => setTimeout(resolve, 1000)); //fake api call
      return Promise.resolve();
    },
    //client side optimistic update
    onMutate: (newTransactions: Transaction[]) => {
      queryClient.setQueryData(['Transactions'], (prevTransactions: any) =>
        prevTransactions?.map((trans: Transaction) => {
          const newTrans = newTransactions.find((u) => u.id === trans.id);
          return newTrans ? newTrans : trans;
        }),
      );
    },
    // onSettled: () => queryClient.invalidateQueries({ queryKey: ['Transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

//DELETE hook (delete user in api)
function useDeleteTrasanction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (transactionId: string) => {
      //send api update request here
      await new Promise((resolve) => setTimeout(resolve, 1000)); //fake api call
      return Promise.resolve();
    },
    //client side optimistic update
    onMutate: (transactionId: string) => {
      queryClient.setQueryData(['trans'], (prevTransactions: any) =>
        prevTransactions?.filter((transaction: Transaction) => transaction.id !== transactionId),
      );
    },
    // onSettled: () => queryClient.invalidateQueries({ queryKey: ['transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

export default TransactionTable;

const validateRequired = (value: string) => !!value?.length;
const validateNumber = (value: number) => !isNaN(Number(value));
const validateEmail = (email: string) =>
  !!email.length &&
  email
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    );

function validateTransaction(transaction: Transaction) {
  return {
    date: !validateRequired(transaction.date) ? 'Date is Required' : '',
    amount: !validateNumber(transaction.amount)
      ? 'Amount is Required'
      : '',
    currencyCode: !validateRequired(transaction.currencyCode)
        ? 'Currency Code is Required'
        : '',
    description: !validateRequired(transaction.description)
        ? 'Description is Required'
        : '',
    category: !validateRequired(transaction.category) ? 'Category is Required' : '',
    
  };
}