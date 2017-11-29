import { authorisationServiceUrl } from '../environmentVariables'

export const SET_CAN_MANAGE_USERS = 'login/SET_CAN_MANAGE_USERS'

const initialState = {
  canManageUsers: false
}

export default (state = initialState, action) => {
  switch (action.type) {
    case SET_CAN_MANAGE_USERS:
      return {
        ...state,
        canManageUsers: action.canManageUsers
      }

    default:
      return state
  }
}

const setCanManagerUsers = (canManageUsers) => {
  return {
    type: SET_CAN_MANAGE_USERS,
    canManageUsers: canManageUsers
  }
}

export const canManageUsers = (idToken) => {
  return (dispatch) => {
    const canManageUsersUrl = `${authorisationServiceUrl()}/canManageUsers`
    return fetch(canManageUsersUrl, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + idToken
      },
      method: 'post',
      mode: 'cors',
      body: JSON.stringify({
        permission: 'Manage Users'
      })
    })
      .then((response) => {
        if (response.status === 401) {
          dispatch(setCanManagerUsers(false))
        } else {
          dispatch(setCanManagerUsers(true))
        }
      })
  }
}
