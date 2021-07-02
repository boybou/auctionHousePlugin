import React,{useState} from "react";
import {postAuction} from "../../requests.js";
import goldNuggetImg from "../../images/Gold_Nugget_JE3_BE2.webp";
import "./post-auction.css";
import Price from "../price-label/price.js";


export default function PostAuction({item,storageId,setItem}){

    const [price,setPrice] = useState(1);


    return <div className={"post-auction"}>
        {item ?
            <>
                <div>
                    {item.name} X {item.amount}
                </div>
                {item.enchantments !== "{}" &&
                <div>
                    {item.enchantments}
                </div>
                }
                <div>
                    <label>Price</label>
                    <input value={price} onChange={(event)=>setPrice(parseInt(event.target.value))}/> <Price amount={price}/>
                </div>

                <button onClick={createAuction}>Post auction</button>
            </>
            :
            <div>Select an item down below to post an auction</div>
        }

    </div>




    function createAuction(){
        let auction = {
            storageId:storageId,
            enchantments:item.enchantments,
            itemHashCode:item.hashCode,
            itemName:item.name,
            price,
            amount:item.amount
        }
        postAuction(auction).then((response)=>{
            console.log(response)
            setItem(null)
            window.location.reload(false);
        }).catch((error)=>console.error(error))
    }



}