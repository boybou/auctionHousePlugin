
import axios from "axios";
import {
    deleteAuctionEndpoint,
    fillAuctionEndpoint,
    getAuctionsEndpoint,
    getStorageInventoryEndpoint,
    postAuctionEndpoint
} from "./endpoints.js";


export async function getAuctions(){

    return axios.get(getAuctionsEndpoint);
}

export async function postAuction(auction){
    return axios.post(postAuctionEndpoint,{auction});
}

export function getStorageInventory(shortStorageId){
    return axios.get(getStorageInventoryEndpoint(shortStorageId));
}

export function fillAuction(fillOrder){
    return axios.post(fillAuctionEndpoint,{fillOrder});
}

export function deleteAuction(auctionUuid){
    return axios.delete(deleteAuctionEndpoint(auctionUuid));
}