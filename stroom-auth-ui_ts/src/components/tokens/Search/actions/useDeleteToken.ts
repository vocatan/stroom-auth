import { useApi } from 'src/api/tokens/useApi';
import { useCallback } from 'react';
import { SearchConfig } from 'src/api/tokens/types';

const useDeleteToken = (
  lastSearchConfig: SearchConfig,
  setSelectedTokenRowId: Function,
  selectedTokenRowId?: string,
) => {
  const { performTokenSearch, deleteToken } = useApi();
  return useCallback(() => {
    if (!!selectedTokenRowId) {
      deleteToken(selectedTokenRowId)
        .then(() => {
          performTokenSearch(lastSearchConfig)
          setSelectedTokenRowId('');
        });
    }
    else console.error("Tried to delete a token when none was provided!")
  }, []
  );
}

export default useDeleteToken;