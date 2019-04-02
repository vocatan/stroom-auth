import { useCallback, useReducer } from "react";

import useApi from "./useApi";
import { useActionCreators } from "./redux";
import { useActionCreators as useAuthenticationActionCreators } from "../../../api/authentication";
import { useApi as useAuthorisationApi } from "../../../api/authorisation";
import { User } from "../types";
import { useRouter } from "../../../lib/useRouter";

interface UserStateApi {
  user?: User;
  setUser: (user: User) => void;
  clearUser: () => void;
}

type UsersState = {
  userBeingEdited?: User;
};

type SetUserAction = {
  type: "set_user";
  user?: User;
};
type ClearUserAction = {
  type: "clear_user";
};

const reducer = (
  state: UsersState,
  action: SetUserAction | ClearUserAction
) => {
  switch (action.type) {
    case "set_user":
      return { ...state, userBeingEdited: action.user };

    default:
      return state;
  }
};

const useUsersState = (): UserStateApi => {
  const [userState, dispatch] = useReducer(reducer, {
    userBeingEdited: undefined
  });
  return {
    user: userState.userBeingEdited,
    setUser: (user: User | undefined) => dispatch({ type: "set_user", user }),
    clearUser: () => dispatch({ type: "clear_user" })
  };
};

/**
 * This hook connects the REST API calls to the Redux Store.
 */
const useUsers = () => {
  const { toggleIsSaving, toggleAlertVisibility } = useActionCreators();
  const { history } = useRouter();

  const { user, setUser, clearUser } = useUsersState();

  /**
   * Deletes the user and then refreshes our browser cache of users.
   */
  // const { updateResults } = useUserSearchActionCreators();
  const { remove: removeUserUsingApi, search } = useApi();
  const deleteUser = useCallback(
    (userId: string) => {
      removeUserUsingApi(userId);
      // .then(() =>
      // search().then(results => updateResults(results))
      // )
    },
    [removeUserUsingApi, search]
  );

  /**
   * Updates the user
   */
  const { change: updateUserUsingApi } = useApi();
  const updateUser = useCallback(
    (user: User) => {
      updateUserUsingApi(user)
        .then(response => {
          // clearUser();// TODO: we're leaving this page so the state will be lost, so we probably don't need to clear the user.

          // dispatch({type:"save_user_being_edited", user:undefined})
          // saveUserBeingEdited(undefined);
          toggleAlertVisibility(true, "User has been updated");
          toggleIsSaving(false);
          history.push("/userSearch");
        })
        .catch(error => {
          toggleIsSaving(false);
        });
    },
    [updateUserUsingApi, clearUser, toggleAlertVisibility, toggleIsSaving]
  );

  /**
   * Creates a user
   */
  const { showCreateLoader } = useActionCreators();
  const { createUser: createAuthorisationUser } = useAuthorisationApi();
  const { add: createUserUsingApi } = useApi();
  const createUser = useCallback(
    (user: User) => {
      createUserUsingApi(user)
        .then(newUserId => {
          toggleIsSaving(true);
          createAuthorisationUser(user.email).then(newUserId => {
            showCreateLoader(false);
            toggleAlertVisibility(true, "User has been created");
            toggleIsSaving(false);
            history.push("/userSearch");
          });
        })
        .catch(error => {
          toggleIsSaving(false);
        });
    },
    [
      createUserUsingApi,
      createAuthorisationUser,
      showCreateLoader,
      toggleAlertVisibility,
      toggleIsSaving
    ]
  );

  /**
   * Fetches a user by id/email, and puts it into the redux state.
   */
  const { fetch: fetchUserUsingApi } = useApi();
  const fetchUser = useCallback(
    (userId: string) => {
      fetchUserUsingApi(userId).then(users => {
        showCreateLoader(false);
        setUser(users[0]);
        // dispatch({type:"save_user_being_edited", user:users[0]})
        // saveUserBeingEdited(users[0]);
      });
    },
    [showCreateLoader, setUser]
  );

  /**
   * Fetches a user by id/email, and puts it into the redux state.
   */
  const { fetchCurrentUser: apiFetchCurrentUser } = useApi();
  const { setCurrentUser } = useAuthenticationActionCreators();
  const fetchCurrentUser = useCallback(() => {
    apiFetchCurrentUser().then(users => {
      setCurrentUser(users[0]);
    });
  }, [setCurrentUser, apiFetchCurrentUser]);

  return {
    // deleteUser,
    updateUser,
    createUser,
    fetchUser,
    fetchCurrentUser,
    user
  };
};
export default useUsers;
