import { Action } from "redux";
import { Token } from "../tokens/types"; //TODO don't reach into other modules! Go via index
import { StoreState } from "./types";
import { genUseActionCreators, prepareReducer } from "src/lib/redux-actions-ts";

const SHOW_SEARCH_LOADER = "tokenSearch/SHOW_SEARCH_LOADER";
const UPDATE_RESULTS = "tokenSearch/UPDATE_RESULTS";
const SELECT_ROW = "tokenSearch/SELECT_ROW";
const TOGGLE_ENABLED = "tokenSearch/TOGGLE_ENABLED";
const CHANGE_LAST_USED_PAGE_SIZE = "tokenSearch/CHANGE_LAST_USED_PAGE_SIZE";
const CHANGE_LAST_USED_PAGE = "tokenSearch/CHANGE_LAST_USED_PAGE";
const CHANGE_LAST_USED_SORTED = "tokenSearch/CHANGE_LAST_USED_SORTED";
const CHANGE_LAST_USED_FILTERED = "tokenSearch/CHANGE_LAST_USED_FILTERED";

interface ShowSearchLoaderAction
  extends Action<"tokenSearch/SHOW_SEARCH_LOADER"> {
  showSearchLoader: boolean;
}
interface UpdateResultsAction extends Action<"tokenSearch/UPDATE_RESULTS"> {
  results: Token[];
  totalPages: Number;
}
interface SelectRowAction extends Action<"tokenSearch/SELECT_ROW"> {
  selectedTokenRowId: String;
}
interface ToggleEnabledAction extends Action<"tokenSearch/TOGGLE_ENABLED"> {
  id: String;
}
interface ChangeLastUsedPageSizeAction
  extends Action<"tokenSearch/CHANGE_LAST_USED_PAGE_SIZE"> {
  lastUsedPageSize: Number;
}
interface ChangeLastUsedPageAction
  extends Action<"tokenSearch/CHANGE_LAST_USED_PAGE"> {
  lastUsedPage: Number;
}
interface ChangeLastUsedSortedAction
  extends Action<"tokenSearch/CHANGE_LAST_USED_SORTED"> {
  lastUsedSorted: String;
}
interface ChangeLastUsedFilteredAction
  extends Action<"tokenSearch/CHANGE_LAST_USED_FILTERED"> {
  lastUsedFiltered: String;
}

const defaultState: StoreState = {
  tokens: [],
  showSearchLoader: false,
  selectedTokenRowId: undefined,
  results: [],
  totalPages: undefined,
  errorStatus: undefined,
  errorText: undefined,
  lastUsedPageSize: undefined
};

export const useActionCreators = genUseActionCreators({
  showSearchLoader: (showSearchLoader: boolean): ShowSearchLoaderAction => ({
    type: SHOW_SEARCH_LOADER,
    showSearchLoader
  }),
  updateResults: (
    results: Token[],
    totalPages: Number
  ): UpdateResultsAction => ({
    type: UPDATE_RESULTS,
    results,
    totalPages
  }),
  selectRow: (selectedTokenRowId: String): SelectRowAction => ({
    type: SELECT_ROW,
    selectedTokenRowId
  }),
  toggleEnabled: (id: String): ToggleEnabledAction => ({
    type: TOGGLE_ENABLED,
    id
  }),
  changeLastUsedPageSize: (
    lastUsedPageSize: Number
  ): ChangeLastUsedPageSizeAction => ({
    type: CHANGE_LAST_USED_PAGE_SIZE,
    lastUsedPageSize
  }),
  changeLastUsedPage: (lastUsedPage: Number): ChangeLastUsedPageAction => ({
    type: CHANGE_LAST_USED_PAGE,
    lastUsedPage
  }),
  changeLastUsedSorted: (
    lastUsedSorted: String
  ): ChangeLastUsedSortedAction => ({
    type: CHANGE_LAST_USED_SORTED,
    lastUsedSorted
  }),
  changeLastUsedFiltered: (
    lastUsedFiltered: String
  ): ChangeLastUsedFilteredAction => ({
    type: CHANGE_LAST_USED_FILTERED,
    lastUsedFiltered
  })
});

export const reducer = prepareReducer(defaultState)
  .handleAction<ShowSearchLoaderAction>(
    SHOW_SEARCH_LOADER,
    (state = defaultState, { showSearchLoader }) => ({
      ...state,
      showSearchLoader
    })
  )
  .handleAction<UpdateResultsAction>(
    UPDATE_RESULTS,
    (state = defaultState, { results, totalPages }) => ({
      ...state,
      results,
      totalPages
    })
  )
  .handleAction<SelectRowAction>(
    SELECT_ROW,
    (state = defaultState, { selectedTokenRowId }) => {
      if (state.selectedTokenRowId === selectedTokenRowId) {
        return {
          ...state,
          selectedTokenRowId: undefined
        };
      } else {
        return {
          ...state,
          selectedTokenRowId: selectedTokenRowId
        };
      }
    }
  )
  .handleAction<ToggleEnabledAction>(
    TOGGLE_ENABLED,
    (state = defaultState, { id }) => ({
      ...state,
      results: state.results.map((result, i) =>
        result.id === id ? { ...result, enabled: !result.enabled } : result
      )
    })
  )
  .handleAction<ChangeLastUsedPageSizeAction>(
    CHANGE_LAST_USED_PAGE_SIZE,
    (state = defaultState, { lastUsedPageSize }) => ({
      ...state,
      lastUsedPageSize
    })
  )
  .handleAction<ChangeLastUsedPageAction>(
    CHANGE_LAST_USED_PAGE,
    (state = defaultState, { lastUsedPage }) => ({
      ...state,
      lastUsedPage
    })
  )
  .handleAction<ChangeLastUsedSortedAction>(
    CHANGE_LAST_USED_SORTED,
    (state = defaultState, { lastUsedSorted }) => ({
      ...state,
      lastUsedSorted
    })
  )
  .handleAction<ChangeLastUsedFilteredAction>(
    CHANGE_LAST_USED_FILTERED,
    (state = defaultState, { lastUsedFiltered }) => ({
      ...state,
      lastUsedFiltered
    })
  );
