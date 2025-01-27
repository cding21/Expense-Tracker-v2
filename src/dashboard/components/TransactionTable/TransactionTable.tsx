import { useMemo, useRef, useState } from 'react';
import {
  MantineReactTable,
  createRow,
  type MRT_ColumnDef,
  type MRT_Row,
  type MRT_TableOptions,
  useMantineReactTable,
} from 'mantine-react-table';
import {
  Accordion,
  AccordionItem,
  ActionIcon,
  Blockquote,
  Box,
  Button,
  Code,
  Divider,
  FileButton,
  Flex,
  Group,
  Paper,
  Text,
  Tooltip,
} from '@mantine/core';
import { modals } from '@mantine/modals';
import { IconInfoCircle, IconTrash } from '@tabler/icons-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { mockTransactionCategories, mockTransactionCurrencyCodes } from '@/mockTransaction';
import { Transaction } from '@/models/transaction.model';
import {
  createTransaction,
  deleteTransaction,
  getTransactions,
  updateTransaction,
} from '@/helper/transaction';

const TransactionTable = () => {
  const [validationErrors, setValidationErrors] = useState<Record<string, string | undefined>>({});
  //keep track of rows that have been edited
  const [editedTransactions, setEditedTransactions] = useState<Record<string, Transaction>>({});

  //call CREATE hook
  const { mutateAsync: createTrans, isPending: isCreatingTransaction } = useCreateTransaction();
  //call READ hook
  const {
    data: fetchedTransactions = [],
    isError: isLoadingTransactionsError,
    isFetching: isFetchingTransactions,
    isLoading: isLoadingTransactions,
  } = useGetTransactions();
  //call UPDATE hook
  const { mutateAsync: updateTrans, isPending: isUpdatingTransaction } = useUpdateTransactions();
  //call DELETE hook
  const { mutateAsync: deleteTrans, isPending: isDeletingTransaction } = useDeleteTrasanction();

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
    await createTrans(values);
    exitCreatingMode();
  };

  const [file, setFile] = useState<File | null>(null);
  const resetRef = useRef<() => void>(null);

  const clearFile = () => {
    setFile(null);
    resetRef.current?.();
  };

  const openCsvUploadModal = () => {
    modals.openConfirmModal({
      title: 'Upload Transactions',
      children: (
        <>
          <Blockquote>
            <Text>
              Upload your transactions as a CSV file. The file should contain the following columns:
            </Text>
            <Divider my="md" />
            <Flex direction="column" gap="xs">
              <Text>
                <Code>Date</Code> - The date of the transaction in the format{' '}
                <Code>DD/MM/YYYY</Code>
              </Text>
              <Text>
                <Code>Amount</Code> - The amount of the transaction in USD
              </Text>
              <Text>
                <Code>Currency Code</Code> - The currency code of the transaction
              </Text>
              <Text>
                <Code>Description</Code> - A description of the transaction
              </Text>
              <Text>
                <Code>Category</Code> - The category of the transaction
              </Text>
            </Flex>
            <Divider my="md" />
            <Text>
              <IconInfoCircle size={20} /> Note: The headers of the CSV file shouldbe removed before
              uploading.
            </Text>
          </Blockquote>
          <Group justify="center" my="md">
            <FileButton onChange={setFile} accept=".csv">
              {(props) => <Button {...props}>Upload file</Button>}
            </FileButton>
            <Button disabled={!file} color="red" onClick={clearFile}>
              Reset
            </Button>
          </Group>
          {file && (
            <Text size="sm" ta="center" mt="sm">
              Picked file: {file.name}
            </Text>
          )}
        </>
      ),
      labels: { confirm: 'Confirm', cancel: 'Cancel' },
      onConfirm: () => {
        //TODO: Implement csv upload
      },
    });
  };

  //UPDATE action
  const handleSaveTransactions = async () => {
    if (Object.values(validationErrors).some((error) => !!error)) return;
    await updateTrans(Object.values(editedTransactions));
    setEditedTransactions({});
  };

  //DELETE action
  const openDeleteConfirmModal = (row: MRT_Row<Transaction>) =>
    modals.openConfirmModal({
      title: '',
      children: (
        <Text>
          Are you sure you want to delete the following transaction?
          <Accordion>
            <AccordionItem key={row.id} title="Transaction Details" value="closed">
              <Accordion.Control icon="💸">{`${row.original.description} - $${row.original.amount}`}</Accordion.Control>
              <Accordion.Panel>
                <Paper radius="md">
                  <Text>
                    <Code>Transaction ID:</Code> {row.original.id}
                  </Text>
                  <Text>
                    <Code>Date:</Code> {row.original.date}
                  </Text>
                  <Text>
                    <Code>Category:</Code> {row.original.category}
                  </Text>
                  <Text>
                    <Code>Currency:</Code> {row.original.currencyCode}
                  </Text>
                  <Text>
                    <Code>Description:</Code> {row.original.description}
                  </Text>
                </Paper>
              </Accordion.Panel>
            </AccordionItem>
          </Accordion>
          This action cannot be undone.
        </Text>
      ),
      labels: { confirm: 'Delete', cancel: 'Cancel' },
      confirmProps: { color: 'red' },
      onConfirm: () => deleteTrans(row.original.id),
    });

  const [transactionAmountMax] = useMemo(() => {
    const amounts = fetchedTransactions.map((transaction) => transaction.amount);
    return [Math.max(...amounts)];
  }, [fetchedTransactions]);

  const columns = useMemo<MRT_ColumnDef<Transaction>[]>(
    () => [
      {
        accessorKey: 'id',
        header: 'Transaction ID',
        enableEditing: false,
        size: 80,
      },
      {
        accessorKey: 'date',
        header: 'Date',
        editVariant: 'text',
        filterVariant: 'date-range',
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
            row.original.date = dayjs(event.currentTarget.value).format('DD/MM/YYYY');
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: {
                ...row.original,
                date: dayjs(event.currentTarget.value).format('DD/MM/YYYY'),
              },
            });
          },
        }),
      },
      {
        accessorKey: 'amount',
        header: 'Amount',
        size: 150,
        filterVariant: 'range-slider',
        mantineFilterRangeSliderProps: {
          color: 'indigo',
          max: transactionAmountMax, //custom max (as opposed to faceted max)
          min: 0,
          step: 1,
          label: (value) =>
            value?.toLocaleString?.('en-US', {
              style: 'currency',
              currency: 'USD',
              minimumFractionDigits: 0,
              maximumFractionDigits: 0,
            }),
        },
        Cell: ({ cell }) => (
          <Box
            style={(theme) => ({
              backgroundColor:
                cell.getValue<number>() > 500
                  ? theme.colors.red[9]
                  : cell.getValue<number>() <= 500 && cell.getValue<number>() > 100
                    ? theme.colors.yellow[9]
                    : theme.colors.green[9],
              borderRadius: '4px',
              color: '#fff',
              maxWidth: '9ch',
              padding: '4px',
            })}
          >
            {cell.getValue<number>()?.toLocaleString?.('en-AU', {
              style: 'currency',
              currency: 'AUD',
              minimumFractionDigits: 0,
              maximumFractionDigits: 0,
            })}
          </Box>
        ),

        mantineEditTextInputProps: ({ cell, row }) => ({
          type: 'number',
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
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, amount: Number(event.currentTarget.value) },
            });
          },
        }),
      },
      {
        accessorKey: 'currencyCode',
        header: 'Currency Code',
        editVariant: 'select',
        filterVariant: 'multi-select',
        mantineEditSelectProps: ({ row }) => ({
          required: true,
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
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, description: event.currentTarget.value },
            });
          },
        }),
      },
      {
        accessorKey: 'category',
        header: 'Category',
        editVariant: 'select',
        mantineEditSelectProps: ({ row }) => ({
          required: true,
          data: mockTransactionCategories,
          //store edited user in state to be saved later
          onChange: (value: any) =>
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, category: value },
            }),
        }),
      },
      {
        accessorKey: 'tags',
        header: 'Tags',
        enableEditing: false,
        size: 150,
      },
      {
        accessorKey: 'fromAccount',
        header: 'From Account',
        mantineEditTextInputProps: ({ row }) => ({
          type: 'text',
          //store edited user in state to be saved later
          onBlur: (event) => {
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, description: event.currentTarget.value },
            });
          },
        }),
      },
      {
        accessorKey: 'fromNote',
        header: 'Note',
        mantineEditTextInputProps: ({ row }) => ({
          type: 'text',
          //store edited user in state to be saved later
          onBlur: (event) => {
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, description: event.currentTarget.value },
            });
          },
        }),
      },
      {
        accessorKey: 'toAccount',
        header: 'To Account',
        mantineEditTextInputProps: ({ row }) => ({
          type: 'text',
          //store edited user in state to be saved later
          onBlur: (event) => {
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, description: event.currentTarget.value },
            });
          },
        }),
      },
      {
        accessorKey: 'toNote',
        header: 'Note',
        mantineEditTextInputProps: ({ row }) => ({
          type: 'text',
          //store edited user in state to be saved later
          onBlur: (event) => {
            setEditedTransactions({
              ...editedTransactions,
              [row.id]: { ...row.original, description: event.currentTarget.value },
            });
          },
        }),
      },
    ],
    [editedTransactions, validationErrors]
  );

  const t = useMantineReactTable({
    columns,
    data: fetchedTransactions,
    createDisplayMode: 'modal', // ('modal', and 'custom' are also available)
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
        {Object.values(validationErrors).map(
          (error, index) =>
            error && (
              <Text key={index} c="red">
                {error}
              </Text>
            )
        )}
      </Flex>
    ),
    renderTopToolbarCustomActions: ({ table }) => (
      <Flex mih={50} gap="xs" justify="flex-start" align="center" direction="row" wrap="wrap">
        <Button
          onClick={async () => {
            // table.setCreatingRow(true); //simplest way to open the create row modal with no default values
            //or you can pass in a row object to set default values with the `createRow` helper function
            table.setCreatingRow(
              createRow(table, {
                id: '',
                userId: '',
                date: dayjs().format('DD/MM/YYYY'),
                amount: 0.0,
                currencyCode: 'AUD',
                description: '',
                category: '',
                tags: [],
                fromAccount: '',
                fromNote: '',
                toAccount: '',
                toNote: '',
              })
            );
          }}
          color="violet"
        >
          Create New Transaction
        </Button>
        <Button
          onClick={() => {
            // TODO: Implement upload transactions via csv file
            openCsvUploadModal();
          }}
        >
          Upload New Transactions (.csv)
        </Button>
      </Flex>
    ),
    initialState: {
      columnVisibility: {
        fromAccount: false,
        fromNote: false,
        toAccount: false,
        toNote: false,
      },
    },
    state: {
      isLoading: isLoadingTransactions,
      isSaving: isCreatingTransaction || isUpdatingTransaction || isDeletingTransaction,
      showAlertBanner: isLoadingTransactionsError,
      showProgressBars: isFetchingTransactions,
    },
  });

  return <MantineReactTable table={t} />;
};

//CREATE hook (post new user to api)
function useCreateTransaction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (trans: Transaction) => {
      //send api update request here
      createTransaction(trans);
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
          ] as Transaction[]
      );
    },
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['Transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

//READ hook (get Transactions from api)
function useGetTransactions() {
  return useQuery<Transaction[]>({
    queryKey: ['Transactions'],
    queryFn: async () => getTransactions(),
    refetchOnWindowFocus: false,
  });
}

//UPDATE hook (put Transactions in api)
function useUpdateTransactions() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (transactions: Transaction[]) => {
      //send api update request here
      transactions.map(async (t) => updateTransaction(t));
    },
    //client side optimistic update
    onMutate: (newTransactions: Transaction[]) => {
      queryClient.setQueryData(['Transactions'], (prevTransactions: any) =>
        prevTransactions?.map((trans: Transaction) => {
          const newTrans = newTransactions.find((u) => u.id === trans.id);
          return newTrans || trans;
        })
      );
    },
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['Transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

//DELETE hook (delete user in api)
function useDeleteTrasanction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (transactionId: string) => {
      //send api update request here
      deleteTransaction(transactionId);
    },
    //client side optimistic update
    onMutate: (transactionId: string) => {
      queryClient.setQueryData(['trans'], (prevTransactions: any) =>
        prevTransactions?.filter((transaction: Transaction) => transaction.id !== transactionId)
      );
    },
    onSettled: () => queryClient.invalidateQueries({ queryKey: ['transactions'] }), //refetch Transactions after mutation, disabled for demo
  });
}

export default TransactionTable;

const validateRequired = (value: string) => !!value?.length;
// const validateDate = (value: string) => dayjs(value, 'dd/mm/yyyy').isValid();
const validateNumber = (value: number) => !Number.isNaN(value);
function validateTransaction(transaction: Transaction) {
  return {
    date: !validateRequired(transaction.date) ? 'Date is Required' : '',
    amount: !validateNumber(transaction.amount) ? 'Amount is Required' : '',
    currencyCode: !validateRequired(transaction.currencyCode) ? 'Currency Code is Required' : '',
    description: !validateRequired(transaction.description) ? 'Description is Required' : '',
    category: !validateRequired(transaction.category) ? 'Category is Required' : '',
  };
}
