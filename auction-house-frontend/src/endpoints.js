const apiUrl = `http://94.210.51.19:3000/api`

export function getStorageInventoryEndpoint(storageId){
    return `${apiUrl}/storage/${storageId}`;
}

export const postAuctionEndpoint = `${apiUrl}/auctions`;

export const getAuctionsEndpoint = `${apiUrl}/auctions`;

export const fillAuctionEndpoint = `${apiUrl}/auctions/fill`;

export function deleteAuctionEndpoint(auctionUuid){
    return `${apiUrl}/auctions/${auctionUuid}`;
}