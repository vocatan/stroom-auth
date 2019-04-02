/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { combineReducers } from "redux";

import { authorisationReducer as authorisation } from "../startup/authentication";
import { reducer as authentication } from "../api/authentication";
import { reducer as config } from "../startup/config";
import { reducer as errorPage } from "../components/ErrorPage";
import { reducer as login } from "../components/users/api";
import { reducer as token } from "../api/tokens";
import { reducer as tokenSearch } from "../api/tokenSearch";
import { reducer as user } from "../components/users/api";

export default combineReducers({
  login,
  errorPage,
  user,
  token,
  tokenSearch,
  authentication,
  authorisation,
  config
});
