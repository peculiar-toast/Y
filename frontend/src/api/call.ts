type FetchMethodTypes = "GET" | "POST" | "DELETE" | "PUT";

const BASE_URL = "http://localhost:8080/api";

async function apiCall(
  url: string,
  method: FetchMethodTypes = "GET",
  data = {},
  headers = {},
) {
  const options: RequestInit = {
    method,
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      ...headers,
    },
  };

  if (method !== "GET") {
    options.body = JSON.stringify(data);
  } else if (Object.keys(data).length > 0) {
    const queryParams = new URLSearchParams(
      data as Record<string, string>,
    ).toString();
    url += `?${queryParams}`;
  }

  try {
    const response = await fetch(`${BASE_URL}${url}`, options);

    if (!response.ok) {
      throw new Error(`API call failed with status ${response.status}`);
    }

    // if empty response
    if (response.status === 204) {
      return {};
    }

    console.log(response);

    return response.json();
  } catch (error) {
    throw new Error(`Network error: ${error}`);
  }
}

export { apiCall };
