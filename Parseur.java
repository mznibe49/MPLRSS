package com.example.simoz.mplrss;

import android.util.Log;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parseur  {

    private String file_name;
    private String url_lien;
    private AccessDonnees ac;

    private ArrayList<Node> efr; // element fichier rss
    private ArrayList<Node> eti; // element item



    public Parseur(String fn, String ul, AccessDonnees ac) {
        this.file_name = fn;
        this.url_lien = ul;
        this.ac = ac;
        efr = new ArrayList<Node>();
        eti = new ArrayList<Node>();
    }

    ArrayList<Node> getFicRssListNode(){
        return this.efr;
    }

    ArrayList<Node> getItemListNode(){
        return this.eti;
    }

    boolean lunch(){
        return createDocument(this.file_name);
    }

    boolean createDocument(String path){
        //String path = path_file.replace("file://","");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document document = null;
        Log.e("real path is : ", path);
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(new File(path));
            Log.e("IN DOC TRY", "enfin !");
            return parseDocument(document);
        } catch (DOMException e){
            Log.d("DOCEXP in createDoc : ",e.getMessage());
        } catch (IOException e){
            Log.d("IOEXP in createDoc : ",e.getMessage());
        } catch (SAXException e){
            Log.d("SAX in createDoc : ",e.getMessage());
        } catch (ParserConfigurationException e){
            Log.d("PARSER in createDoc : ",e.getMessage());
        }
        return false;
    }

    boolean parseDocument(Document document){
        Element racine = document.getDocumentElement();

        NodeList racineNoeuds = null;//racine.getChildNodes();
        try{
            racineNoeuds = racine.getChildNodes();
        } catch (Exception e){
            Log.d("ERR in ParseDoc"," :s :s ");
        }
        String racine_name = racine.getNodeName();
        Log.d("Racine name : ",racine_name);
        if(racine_name.equals("rss")) {
            // if ( le fichier est un fichier rss apres le parcours des elements !!
            int nbRacineNoeuds = racineNoeuds.getLength();

            Node node = null; // channel la premiere balise apres rss
            for (int i = 0; i < nbRacineNoeuds; i++) {
                if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    node = racineNoeuds.item(i);
                    //Log.d("Channel ? ",node.getNodeName());
                }
            }
            NodeList fils = node.getChildNodes(); // les balises dans channel
            int nb_fils = fils.getLength();

            boolean first_item_found = false;
            for (int j = 0; j < nb_fils; j++) {
                if (fils.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    final Node node1 = fils.item(j);
                    //Log.d("Node : ", node1.getNodeName());
                    String node1_name = node1.getNodeName(); // on cherche le nom du fils

                    if (node1_name.equals("item")) first_item_found = true;

                    if (!first_item_found) efr.add(node1);
                    else eti.add(node1);
                }
            }
            return true;
        }
        return false;
    }

    void ajouterDansFicRss(ArrayList<Node> list){

        String lien = "";
        String titre = "";
        String desc = "";
        String dm = ""; // date pub ou date de changement

        for(int i = 0; i<list.size(); i++){
            Node node = list.get(i);
            Element element = (Element) node;
            String node_name = node.getNodeName();
            String contenu_node = element.getTextContent();
            switch (node_name){
                case "title":
                    titre = contenu_node;
                    break;
                case "description":
                    desc = contenu_node;
                    break;
                case "link":
                    lien = this.url_lien;
                    break;
                case "pubDate":
                    dm = contenu_node;
                    break;
                case "lastBuildDate":
                    dm = contenu_node;
                    break;
            }
        }
        this.ac.ajoutRss(lien,titre,desc,dm);

    }

    public Node filtreNode(Node node){
        NodeList list = node.getChildNodes();
        return null;
    }

    void ajouterDansItem(ArrayList<Node> list){

        String adresse = "";
        String titre = "";
        String desc = "";
        String dm = ""; // date pub ou date de changement
        Log.d("Nbr d'item : ",""+list.size());
        //String lien

        for(int i = 0; i<list.size(); i++){

            NodeList list_node = list.get(i).getChildNodes(); // chaque item a une liste d autre element
            //Log.d("nbr balise dans item : ",""+list_node.getLength());
            for(int j = 0; j<list_node.getLength();j++){
                Node node = list_node.item(j);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    if(node.getNodeName().equals("description")){
                        //node = filtreNode(node); // pour enlever les </br> , <img>, etc..
                        if(node.hasChildNodes()){
                            Log.d("First Node ",node.getFirstChild().getNodeType()+"");
                        }
                    }
                    Element element = (Element) node;
                    String node_name = node.getNodeName();
                    String contenu_node = element.getTextContent();
                    switch (node_name) {
                        case "title":
                            titre = contenu_node;
                            break;
                        case "description":
                            desc = contenu_node;
                            break;
                        case "link":
                            adresse = contenu_node;
                            break;
                        case "pubDate":
                            dm = contenu_node;
                            break;
                        case "lastBuildDate":
                            dm = contenu_node;
                            break;
                    }
                }
            }
            this.ac.ajoutItem(this.url_lien,adresse,titre,desc,dm);
        }
    }

    /*
    on prend url lien
    on verifie s'il existe dans la base
        on verifie la date dans la base(de cet url) et la date dans la liste des Node du fichier rss
        si la premiere date est egale a l'ancienne
            on va ouvrir le fichier rss
        sin
            on supprime l'ancien fichier rss de la base
            on ajoute le nouveau fichier rss de la base
            on ouvre le nouveaux fichier rss
    sin on ajoute directement dans la base
     */
    // ici on verifie l'existance de l'url dans la mainAc

    boolean checkDate(){
        String str_node_date= "" ;
        String format_pattern = "";
        for(int i =0; i<efr.size();i++){
            Node node = efr.get(i);
            Element element = (Element) node;
            String node_name = node.getNodeName();
            String contenu_node = element.getTextContent();
            if(node_name.equals("pubDate") || node_name.equals("lastBuildDate")){
                str_node_date = contenu_node;
                //if(node_name.equals("pubDate")) format_pattern  = "EEE, d MMM yyyy HH:mm:ss Z";
                //else if(node_name.equals("lastBuildDate")) format_pattern = "yyyy-MM-dd HH:mm:ss";
            }
        }
        //SimpleDateFormat dateFormat = null;

        try {
            Date nvDate = convertirDate(str_node_date);
            String ancieneDate = this.ac.getDateFromRss(this.url_lien);
            Date ancDate = convertirDate(ancieneDate);
            Log.e("Print Date ","anc date : "+ancDate.getTime()+" nv date "+nvDate.getTime());
            if(nvDate.getTime() == ancDate.getTime()) return true;
        } catch (Exception e){
            Log.e("ERR in CheckDate",e.getMessage());
        }
        return false;
    }

    public Date convertirDate(String date){
        SimpleDateFormat dateFormat1 = null;
        SimpleDateFormat dateFormat2 = null;
        Date dateObj = null;
        try {
            dateFormat1 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String [] tab = date.split(" ");
            if(tab.length > 2){
                String res = "";
                for(int i = 1; i<tab.length-1;i++){
                    if (i == tab.length-2) res+=tab[i];
                    else res += tab[i]+" ";
                }
                dateObj = dateFormat1.parse(res);
            } else dateObj = dateFormat2.parse(date);

        } catch (Exception e) {
            Log.e("in convDate ","try de date "+e.getMessage());
        }
        return dateObj;
    }
}