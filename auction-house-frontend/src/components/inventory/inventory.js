import React, {useState,useEffect} from "react";
import "./inventory.css";
import StorageSelection from "../storage-selection/storage-selection.js";
import {getStorageInventory} from "../../requests.js";
import PostAuction from "../post-auction/post-auction.js";
import Price from "../price-label/price.js";

export default function Inventory({auctions,inventory,setInventory,fullStorageId,setFullStorageId}){




    const [selectedItem,setSelectedItem] = useState(null);
    const [cashAmount,setCashAmount] = useState(0);


    return <div className={"inventory"}>
        {inventory.length > 0 || cashAmount > 0 ?
            <>
                <PostAuction item={selectedItem} storageId={fullStorageId} setItem={setSelectedItem} />
                <div className={"misc"}>
                    <label>Balance: </label>
                    <Price  amount={cashAmount}/>
                    <button onClick={changeStorage}>Change storage</button>
                </div>

                <ul >
                    {renderItems()}
                </ul>
            </>

        :
        <StorageSelection getStorage={getStorage}/>
        }

    </div>

    function changeStorage(){
        localStorage.removeItem("storageId")
        setInventory([]);
    }

    function getStorage(storageId){
        getStorageInventory(storageId).then((response)=>{
            let inventory = response.data.inventory;
            setCashAmount(inventory.filter(item=>{
                return item.name === "GOLD_INGOT" || item.name === "GOLD_NUGGET";
            }).reduce((a,v)=>{
                if(v.name === "GOLD_INGOT") return a+(9*v.amount);
                else if(v.name === "GOLD_NUGGET") return a+v.amount;
                else return a;
            },0))
            inventory = inventory.filter(item=>item.name !== "GOLD_NUGGET" && item.name !=="GOLD_INGOT")
            setInventory(inventory)
            setFullStorageId(response.data.storageId)
        }).catch((error)=>console.error(error))
    }

    function renderItems(){
        return inventory.map((item,index)=>{
            return <li key={index} onClick={()=>setSelectedItem(item)}>
                <div>
                    {item.name} X {item.amount}
                </div>
                {item.enchantments !== "{}" &&
                <div>
                    {item.enchantments}
                </div>
                }

            </li>
        })
    }



}