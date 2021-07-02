import React from "react";
import {useState,useEffect} from 'react';

export default function StorageSelection({getStorage}){



    const [storageId,setStorageId] = useState("");

    useEffect(()=>{
        let storedStorageId = localStorage.getItem("storageId");
        if(storedStorageId){
            setStorageId(storedStorageId);
            getStorage(storedStorageId);
        }
    },[])

    useEffect(()=>{
        if(storageId){
            localStorage.setItem("storageId",storageId);
        }
    },[storageId])

    return <div>
        <label>Storage id</label>
        <input value={storageId} onChange={(event)=>{
            setStorageId(event.target.value);
        }}/>
        <button onClick={()=>{
            getStorage(storageId);
        }}>GO!!!</button>
    </div>



}