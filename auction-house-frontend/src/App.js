import './App.css';
import StorageSelection from "./components/storage-selection/storage-selection.js";
import {useState} from 'react';
import {getStorageInventory} from "./requests.js";
import Inventory from "./components/inventory/inventory.js";
import Main from "./components/main/main.js";


function App() {



  return <div className={"app"}>
        <Main />
  </div>


}

export default App;
