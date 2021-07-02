import React,{useState,useEffect} from "react";
import "./auctions.css";
import Price from "../price-label/price.js";
import {deleteAuction, fillAuction} from "../../requests.js";

export default function Auctions({auctions,fullStorageId}){

    const [filter,setFilter] = useState("");
    const [filteredAuctions,setFilteredAuctions] = useState(auctions);
    const [windowHidden,setWindowHidden] = useState(true);
    const [selectedAuction,setSelectedAuction] = useState(null);
    const [orderStatus,setOrderStatus] = useState(null);
    const [onlyOwnAuctions,setOnlyOwnAuctions] = useState(false);

    useEffect(()=>{
        let processedAuctions = auctions;
        if(filter){
            processedAuctions = processedAuctions.filter(auction=>{
                return auction.itemName.toLocaleLowerCase().includes(filter.toLocaleLowerCase()) || auction.enchantments.toLowerCase().includes(filter.toLocaleLowerCase());
            })
        }
        if(onlyOwnAuctions){
            processedAuctions = processedAuctions.filter(auction=>{
                return auction.storageUuid === fullStorageId;
            })
        }
        setFilteredAuctions(processedAuctions);

    },[auctions,filter,onlyOwnAuctions])

    return <div className={"auctions"}>
        {!windowHidden &&
            <div className={"order-fill-window-wrapper"}>
                <div className={"order-fill-window"}>
                    <div className={"close"} onClick={()=> {
                        setWindowHidden(true)
                        setSelectedAuction(null)
                    }}>X</div>
                    {selectedAuction &&
                    <div className={"body"}>
                        <h2>BUYING:</h2>
                        <div>
                            {selectedAuction.itemName} X {selectedAuction.amount}
                        </div>
                        {selectedAuction.enchantments !== "{}" &&
                        <div>
                            {selectedAuction.enchantments}
                        </div>
                        }

                        <Price text={"Price:"} amount={selectedAuction.price}/>
                        <div>
                            from: {selectedAuction.storageUuid}
                        </div>
                        <button onClick={fillAuctionOrder}>CONFIRM</button>
                        {orderStatus &&
                        <div className={"order-status"}>{orderStatus}</div>
                        }

                    </div>
                    }
                </div>
            </div>

        }

        <div className={"header"}>
            <input value={filter} onChange={(event => setFilter(event.target.value))} placeholder={"search..."} />
            <button onClick={()=>setOnlyOwnAuctions(old=>!old)}>{(onlyOwnAuctions) ? "Show all auctions":"Show my auctions"}</button>
        </div>
        <div className={"list"}>
            <ul>
                {renderAuctions()}
            </ul>
        </div>

    </div>

    function fillAuctionOrder(){
        fillAuction({
            auctionUuid:selectedAuction.uuid,
            payingStorageUuid:fullStorageId
        }).then(response=>{
            setOrderStatus("ORDER COMPLETE");
        }).catch((error)=>{
            console.log(error.response)
            setOrderStatus("ORDER FAILED! ITEMS MIGHT NOT BE IN STORAGE OR YOU ARE OUT OF FUNDS!!!"+JSON.stringify(error.response.data));
        })
    }

    function renderAuctions(){
        return filteredAuctions.map(auction=>{
           return <li key={auction.uuid} onClick={()=> {
               setWindowHidden(old => !old)
               setSelectedAuction(auction);
           }}>
                <div>
                    {auction.itemName} X {auction.amount}
                </div>
               {auction.enchantments !== "{}" &&
               <div>
                   {auction.enchantments}
               </div>
               }

               <Price text={"Price:"} amount={auction.price}/>
               <div>
                   from: {auction.storageUuid}
               </div>
               {auction.storageUuid === fullStorageId &&
                    <button onClick={()=>{
                        deleteAuction(auction.uuid).then((response)=>{
                            window.location.reload(false);
                        })
                            .catch((error)=>console.error(error));
                    }
                    }>Remove auction</button>

               }


            </li>
        })
    }

}