import axios from "axios";

const API_URL = "http://localhost:8082/api/offices";

// READ: GET /api/offices -> array of offices
export async function getOffices() {
  const res = await axios.get(API_URL);
  return res.data;
}

// DELETE: DELETE /api/offices/{id}
export async function deleteOffice(id) {
  await axios.delete(`${API_URL}/${id}`);
}
