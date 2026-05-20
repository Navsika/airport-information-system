import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'

export const useEntity = (entityName, service) => {
    const queryClient = useQueryClient();

    const useGetAll = (params, options = {}) => useQuery({
        queryKey: [entityName, params],
        queryFn: () => service.getAll(params),
        keepPreviousData: true,
        ...options,
    });

    const useGetList = (options = {}) => useQuery({
        queryKey: [entityName, 'list'],
        queryFn: service.getList,
        staleTime: 5 * 60 * 1000,
        ...options,
    });

    const useGetById = (id, options = {}) => useQuery({
        queryKey: [entityName, id],
        queryFn: () => service.getById(id),
        enabled: !!id,
        ...options,
    });

    const useCreate = () => useMutation({
        mutationFn: service.create,
        onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [entityName] });
        },
    });

    const useUpdate = () => useMutation({
        mutationFn: ({ id, data }) => service.update(id, data),
        onSuccess: (_, { id }) => {
        queryClient.invalidateQueries({ queryKey: [entityName] });
        queryClient.invalidateQueries({ queryKey: [entityName, id] });
        },
    });

    const useRemove = () => useMutation({
        mutationFn: service.delete,
        onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [entityName] });
        },
    });

    return {
        useGetAll,
        useGetList,
        useGetById,
        useCreate,
        useUpdate,
        useRemove,
    };
};