package auctionHouse.repository;

import auctionHouse.entity.Auction;



public class AuctionRepository extends Repository<Auction> {

    public AuctionRepository() {
        super("auctions");
    }

}