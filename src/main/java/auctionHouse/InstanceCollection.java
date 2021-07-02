package auctionHouse;

import auctionHouse.repository.AuctionRepository;
import auctionHouse.repository.StorageRepository;
import auctionHouse.service.AuctionService;
import auctionHouse.service.StorageService;

public class InstanceCollection {

    public static AuctionRepository auctionRepository;
    public static AuctionService auctionService;
    public static StorageService storageService;
    public static StorageRepository storageRepository;

    public static void instantiate(){
        InstanceCollection.auctionService = new AuctionService();
        InstanceCollection.auctionRepository = new AuctionRepository();
        InstanceCollection.storageRepository = new StorageRepository();
        InstanceCollection.storageService = new StorageService();
    }

}
