import React,{useState,useEffect} from "react";

import nuggetImg from "../../images/Gold_Nugget_JE3_BE2.webp";
import ingotImg from "../../images/Gold_Ingot_JE4_BE2.webp";

export default function Price({amount,text}){

    const [goldIngotAmount,setGoldIngotAmount] = useState(0);
    const [goldNuggetAmount,setGoldNuggetAmount]= useState(0);

    useEffect(()=>{
        let fit = Math.floor(amount/9);
        let rest = amount - (fit*9);
        setGoldIngotAmount(fit);
        setGoldNuggetAmount(rest);
    },[amount])

    return <div>
        {text} {goldIngotAmount} X <img src={ingotImg}/>  {goldNuggetAmount} X <img src={nuggetImg}/>
    </div>

}