import java.util.Arrays;

public class vigenere_cipher {
	public static void main(String[] args) {
		String s = "XOLRV OLNNJ QFPAL FCMEE LNIVA RISKA EEXJG HEHYE DHWLI BPDLS MRPAE " 
			     + "ONUGL OGYBH ZAAWR VOPCU ZGYLT BNVDL SVRNP PVYQV VMEFB UAHAM EGJPN " 
			     + "NYAMZ UHQDF LHCXG YQIFY GKHIN UCTLZ CAIHP RCNNM ZONNI LZFNU GNCAH " 
			     + "QEHYY IAEHD TCALB YENJQ ASOOF CUOTB VTAPE HGJLS IAUHS JIHTR YZJYP " 
			     + "VAZOE ZQUEH MBHWW AHAKU RAWPQ YOIHT VVLRY QFPEP IFVIJ OHRQM EHYGG " 
			     + "HXMYZ DLCSN UGSLU HPJDL SHRCY WYWNP JPLFR FIPCU HULZF NUGOT GBPQZ " 
			     + "EOZUG STUGN VASEN VOLMU NGJLA RIWGJ EEPRP AFAFY AAZOE BHMEH YPCTP " 
			     + "RUJJP NHQNU HWSIB WAQIN GGKHI NUCZX ALGRO ZNYGJ HETLN ERPDN UGNZP " 
			     + "LBUSZ CUGKV YWUFN HFNWU GKYON SCYQR IZVOP GLNPK NAHLQ UEHYC JVYEQ " 
			     + "NUWCO AECTX EXFQA SANVV DZUFQ PVEIZ LVOPG LBWWZ NWRKA QEFYD HNKXB " 
			     + "YUEOY NTASW CGJPY CYYNW SOHRT HYGYG JLRPM RPHML YQROZ NYUQD PVYEP " 
			     + "LGELA QATFC RFASE AEQBA AVBWA TTMYQ JLTCB PASEZ VXLQR CRPKD FCTWY " 
			     + "PDNUG FSAXY QZETB REVYT LNRAT OHSQY ROIQD LNAOF GASES UCKXI MPCSN " 
			     + "UFNVL OTBRE HXELN UACAD REAZR SVVAF RHRFV FTBBY LGELG JHETB RADPR "
			     + "YAVDC OHTCZ EHYPQ UERUC VPZNJ YWTXE NRFAZ TBRIY ZUHQV OPPBB PLWOM " 
			     + "GKADS CTPHW BYPCB DECGN HYDYQ KULNU EGHHI NUNPX INRFJ ZVYEC NPWYC " 
			     + "NHYNY QQBCJ OAGAH ONUQB DAHQV OTRNR GUWAO AEOLT UFRLN IZVEA TMYNP " 
			     + "KALUP GZFCB GJHET BRROZ NYJCZ ARIWG JEEXG QSLNX VPHYA LRCDT TBPGS " 
			     + "WCIIG YLGYG GHXMY ZDLCK HBYUL SNEGE LRGFU ZHRIG GASEJ EQIWE GJCZE " 
			     + "HUGVO PCIIG YLGYZ CWHEQ RTLCE FLKUR OHYQV VIHTC AJOON VAHAM AQALC " 
			     + "WHTHE EMBVO PPBBP LYEPR TNZTM VIULL UFKAN AGRDH NKNBG HCTBN PKHEH " 
			     + "RXLCH YNTKQ RIZKA LCWBT KTNAG QASEL RFKTT JBUAE HYPCT PRUYC UOEXN " 
			     + "RWCOR VOHEE FLGPR HNLMP WOGRV YPSZE QTEHY YCBYC BCQPY TCACA HIMGQ " 
			     + "MQANR VOPGI CTVHA MSQBY DVLCD ZMUAY OZWUF JPVIH TVOCO OTJAS EUEGH " 
			     + "CEWRP AWYNU GOTKY EYOZA FFQOL PJRPL OTIOG HYANG GTALI LGLEO IXVOP " 
			     + "PBBPL EOUFV VCEQU GYPTB RAACA WRFPE TIVVZ ZWHRT BDIHT VOPSC ZEHCD " 
			     + "NUGWS OHREH XELNC UOTBR UAFNH VPNQO IGCNP WUFTL EULAG KEONU GNCOO " 
			     + "CQMQR CRPKD WBBNH EELCW IWIMU GKEHY IKKPO IAAVF TOOG";
		
		// key was initially "AAAAAA" to keep crypted text untouched 
		String key = "LAUNCH";
		String s2 = s.replace(" ", "");
		String output = "";
		int temp = 0;
		
		// s - key;
		for(int i = 0; i < s2.length(); i++){
			temp = (s2.charAt(i) - key.charAt(i % key.length()));
			if(temp < 0){
				temp = 90 + temp + 1;
			}else{
				temp += 65;
			}	
			//System.out.println((char) temp);
			output += (char) temp;
		}
		
		for(int i = 0; i < key.length(); i++){
			System.out.println("Frequency for Index " + i);
			String frequency = freq(s2, i);
			System.out.println();
			System.out.println("---------------------------------");
		}
		
		for(int i = 0; i < output.length(); i++){
			System.out.print(output.charAt(i));
			
			// toggle on and off to check each group of 6 letter are being 
			// decyphered correctly
			/*if(i % 6 == 5){
				System.out.print(" ");
			}*/
		}
		
		
	}
	
	static public String freq(String s, int index){
		int[] alphabet = new int[26];
		
		String frequency = "";
		
		for(int i = 0; i < s.length(); i++){
			if(i % 6 == index){
					alphabet[s.charAt(i)-65]++;
			}else{
						
			}
		}
		
		for(int i = 0; i < alphabet.length; i++){
			frequency += ((char)(i+65) + ": " + alphabet[i] + "\t");
			
			if(i % 6 == 5){
				frequency += "\n";
			}
		}
		
		
		System.out.println(frequency);
		
		return frequency;
	}
}
