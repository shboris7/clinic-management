import { useEffect, useState } from "react";
import { getOffices, deleteOffice } from "../services/officeService";
import "./OfficeList.css";

export default function OfficeList() {
  const [offices, setOffices] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Load all offices once, when the page opens
  useEffect(() => {
    loadOffices();
  }, []);

  async function loadOffices() {
    try {
      setLoading(true);
      setError("");
      const data = await getOffices();
      setOffices(data);
    } catch {
      setError("Could not load offices. Is the backend running on port 8082?");
    } finally {
      setLoading(false);
    }
  }

  // SEARCH: keep only offices whose name OR office number contains the text
  const text = search.trim().toLowerCase();
  const filteredOffices = offices.filter(
    (office) =>
      (office.name ?? "").toLowerCase().includes(text) ||
      (office.officeNumber ?? "").toLowerCase().includes(text)
  );

  // DELETE: ask the API, then remove that office from the list on screen
  async function handleDelete(id, name) {
    if (!window.confirm(`Delete office "${name}"?`)) return;

    try {
      await deleteOffice(id);
      setOffices(offices.filter((office) => office.id !== id));
    } catch {
      alert(
        "Could not delete this office. It may still have doctors assigned to it."
      );
    }
  }

  return (
    <div className="office-page">
      <h1>Offices</h1>

      <input
        type="text"
        className="search-box"
        placeholder="Search by office name or number"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {loading && <p>Loading offices...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Office Name</th>
              <th>Office Number</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filteredOffices.length === 0 ? (
              <tr>
                <td colSpan="4" className="empty">
                  No offices found.
                </td>
              </tr>
            ) : (
              filteredOffices.map((office, index) => (
                <tr key={office.id}>
                  <td>{index + 1}</td>
                  <td>{office.name}</td>
                  <td>{office.officeNumber}</td>
                  <td>
                    <button className="btn-update">Update</button>
                    <button
                      className="btn-delete"
                      onClick={() => handleDelete(office.id, office.name)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}
