import { useState, useEffect, useCallback } from 'react';
import { getVehicleById } from '../api/vehicleApi';
import { toast } from 'sonner';

export function useVehicles(fetchFn, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const execute = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchFn();
      setData(result);
      return result;
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Request failed');
      throw err;
    } finally {
      setLoading(false);
    }
  }, deps);

  useEffect(() => { execute(); }, [execute]);

  return { data, loading, error, refetch: execute };
}

export function useVehicleDetail(id) {
  const [vehicle, setVehicle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getVehicleById(id)
      .then(({ data }) => setVehicle(data))
      .catch(() => {
        setError('Vehicle not found');
        toast.error('Vehicle not found');
      })
      .finally(() => setLoading(false));
  }, [id]);

  return { vehicle, loading, error };
}
