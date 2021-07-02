import React from "react";
import Inventory from "../inventory/inventory.js";
import {getAuctions} from "../../requests.js";
import {useEffect,useState} from "react";
import Auctions from "../auctions/auctions.js";
import "./main.css";

export default function Main(){

    const [auctions,setAuctions] = useState([]);
    const [inventory,setInventory] = useState([]);
    const [fullStorageId,setFullStorageId] = useState("");

    useEffect(()=>{
       getAuctions().then((response)=>{
           setAuctions(response.data.auctions)
       }).catch((error)=>console.error(error))
    },[])

    return <div className={"main"}>
        <div className={"left-side"}>
            <Inventory fullStorageId={fullStorageId} setFullStorageId={setFullStorageId} setInventory={setInventory} inventory={inventory} auctions={auctions}/>
        </div>
        <div className={"right-side"}>
            <Auctions fullStorageId={fullStorageId} auctions={auctions} />
        </div>
    </div>





}