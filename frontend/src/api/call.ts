import { getAccessToken, getRefreshToken } from "./auth";

type FetchMethodTypes = "GET" | "POST" | "DELETE" | "PUT";

const BASE_URL = "http://192.168.1.100:8000/api";

async function apiCall(
  url: string,
  method: FetchMethodTypes = "GET",
  data: Record<string, unknown> | FormData = {},
  headers: Record<string, string> = {},
) {
  const isFormData = data instanceof FormData;

  const token = getAccessToken()
  const refresh = getRefreshToken()

  const options: RequestInit = {
    method,
    headers: {
      ...(isFormData ? {} : { "Content-Type": "application/json" }),
      ...headers,
      ...(token ? { "Authorization": `Bearer ${token}` } : {})
    },
  };

  if (method !== "GET") {
    options.body = isFormData ? data : JSON.stringify(data);
  } else if (!isFormData && Object.keys(data).length > 0) {
    const queryParams = new URLSearchParams(
      data as Record<string, string>,
    ).toString();

    url += `?${queryParams}`;
  }

  try {
    const response = await fetch(`${BASE_URL}${url}`, options);


    if (response.status === 201) {
      return response.headers.get("Location");
    }

    // if empty response
    if (response.status === 204) {
      return {};
    }

    if (response.status === 401 && refresh) {
      // Attempt to refresh the token
      const refreshResponse = await fetch(`${BASE_URL}/auth/token/refresh/`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ refresh: refresh }),
      });

      if (!refreshResponse.ok) {
        throw new Error(`Token refresh failed with status ${refreshResponse.status}`);
      }

      const refreshData = await refreshResponse.json();
      const newAccessToken = refreshData.access;

      // Store the new access token
      localStorage.setItem("access_token", newAccessToken);

      // Retry the original request with the new access token
      options.headers["Authorization"] = `Bearer ${newAccessToken}`;
      const retryResponse = await fetch(`${BASE_URL}${url}`, options);

      if (!retryResponse.ok) {
        throw new Error(`API call failed after token refresh with status ${retryResponse.status}`);
      }

      return retryResponse.json();
    }

    if (!response.ok) {
      throw new Error(`API call failed with status ${response.status}`);
    }

    return response.json();
  } catch (error) {
    throw new Error(`Network error: ${error}`);
  }
}

export { apiCall };
