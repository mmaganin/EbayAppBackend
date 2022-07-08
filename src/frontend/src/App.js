import Header from "./components/Header";
import Footer from "./components/Footer";
import Ebay from "./components/Ebay";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

function App() {
  return (
    <>
    {/* //   <div class="set-footer-height"> */}
        {/* <Header /> */}
        <Ebay />
        {/* <Router>
          <Header />
          <Routes>
            <Route exact path="/" element={<Ebay />} />
          </Routes>
        </Router> */}
      {/* // </div>
      // <Footer /> */}
    </>
  );
}

export default App;
