import type { JSX } from "react";
import "./styles/index.scss";
import "bootstrap/dist/css/bootstrap.min.css";
import { HomePage } from "./pages/HomePage";

function App(): JSX.Element {
  return (
    <>
      <HomePage />
    </>
  );
}

export default App;
