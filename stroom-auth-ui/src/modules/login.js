import { push } from 'react-router-redux'
import { HttpError } from '../ErrorTypes'

export const EMAIL_CHANGE = 'login/EMAIL_CHANGE'
export const PASSWORD_CHANGE = 'login/PASSWORD_CHANGE'
export const TOKEN_CHANGE = 'login/TOKEN_CHANGE'
export const TOKEN_DELETE = 'login/TOKEN_DELETE'
export const ERROR_ADD = 'login/ERROR_ADD'
export const ERROR_REMOVE = 'login/ERROR_REMOVE'
export const SHOW_LOADER = 'login/SHOW_LOADER'

const initialState = {
  token: '',
  errorStatus: -1,
  errorText: '',
  showLoader: false
}

export default (state = initialState, action) => {
  switch (action.type) {
    case EMAIL_CHANGE:
      return {
        ...state,
        email: action.email
      }

    case TOKEN_CHANGE: 
      return {
        ...state,
        token: action.token
      }
    case TOKEN_DELETE:
      return {
        ...state,
        token: ''
      }
    case ERROR_ADD:
      return {
        ...state,
        errorStatus: action.status,
        errorText: action.text
      }
    case ERROR_REMOVE:
      return {
        ...state,
        errorStatus: -1,
        errorText: ''
      }
    case SHOW_LOADER:
      return {
        ...state,
        showLoader: action.showLoader
      }
    default:
      return state
  }
}

export function changeToken(token) {
  return {
    type: TOKEN_CHANGE,
    token: token
  }
}

export function deleteToken() {
  return {
    type: TOKEN_DELETE
  }
}

export const logout = () => {
    return dispatch => {
      dispatch(deleteToken())
    }
}


export function errorAdd(status, text){
  return {
    type: ERROR_ADD,
    status,
    text
  }
}

export function errorRemove(){
  return {
    type: ERROR_REMOVE
  }
}

export function showLoader(showLoader){
  return {
    type: SHOW_LOADER,
    showLoader
  }
}

export const attempLogin = (email, password, referrer) => {
  return dispatch => {
    // We're re-attempting a login so we should remove any old errors
    dispatch(errorRemove())

    // We want to show a preloader while we're making the request. We turn it off when we receive a response or catch an error.
    dispatch(showLoader(true))

    var loginServiceUrl = process.env.REACT_APP_LOGIN_URL
    // Call the authentication service to get a token.
    // If successful we re-direct to Stroom, otherwise we display a message.
    fetch(loginServiceUrl, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      method: 'post',
      mode: 'cors',
      body: JSON.stringify({
        email,
        password
      })
    })
      .then(handleStatus)
      .then(getBody)
      .then(jwsToken => processToken(jwsToken, dispatch, referrer))
      .catch(error => handleErrors(error, dispatch))
  }
}

function handleStatus(response) {
  if(response.status === 200){
    return Promise.resolve(response)
  } else {
    return Promise.reject(new HttpError(response.status, response.statusText))
  }
}

function getBody(response) {
  return response.text()
}

function processToken(token, dispatch, referrer){
  dispatch(changeToken(token))
  dispatch(showLoader(false))        
  if(referrer === "stroom"){
    //TODO use authorisation header
    var loginUrl = process.env.REACT_APP_STROOM_UI_URL + '/?token=' + token
    window.location.href = loginUrl
  }
  else if(referrer === "") {
    dispatch(push('/'))
  }
  else {
    dispatch(push(referrer))
  }
}

function handleErrors(error, dispatch) {
  dispatch(showLoader(false))
  if(error.status === 401){
    dispatch(errorAdd(error.status, 'Those credentials are not valid. Please try again.'))
  }
  else { 
    dispatch(errorAdd(error.status, error.message))
  }
}