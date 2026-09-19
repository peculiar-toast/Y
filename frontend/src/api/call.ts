type FetchMethodTypes = "GET" | "POST" | "DELETE" | "PUT";

const BASE_URL = "http://localhost:8000/api";

async function apiCall(
  url: string,
  method: FetchMethodTypes = "GET",
  data: Record<string, unknown> | FormData = {},
  headers: Record<string, string> = {},
) {
  const isFormData = data instanceof FormData;

  const options: RequestInit = {
    method,
    headers: {
      ...(isFormData ? {} : { "Content-Type": "application/json" }),
      ...headers,
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

    if (!response.ok) {
      throw new Error(`API call failed with status ${response.status}`);
    }

    if (response.status === 201) {
      return response.headers.get("Location");
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
