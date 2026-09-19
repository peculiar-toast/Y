import type { JSX } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "./styles/index.scss";
import { HomePage } from "./pages/HomePage";

function App(): JSX.Element {
  return (
    <>
      <HomePage />
    </>
  );
}

export default App;
