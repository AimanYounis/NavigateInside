package navigate.inside.Logic;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import java.util.ArrayList;
import java.util.UUID;

import navigate.inside.Network.NetworkConnector;
import navigate.inside.Objects.BeaconID;
import navigate.inside.Objects.Node;
import navigate.inside.Objects.Room;
import navigate.inside.Utills.Constants;

public class SysData {
    //Its should contain Nodes List
    private static SysData instance = null;
    private ArrayList<Node> AllNodes;
    private DataBase db;

    private SysData(){
        AllNodes = new ArrayList<>();
        //InitializeData();
    }

    public static SysData getInstance(){
        if(instance == null){
            instance = new SysData();
        }
        return instance;
    }

    public ArrayList<Node> getAllNodes(){
        return AllNodes;
    }

    public BeaconID getNodeIdByRoom(String room){
        String[] BothItems = room.split("-");
        if (BothItems.length > 1){
            Room r = new Room(BothItems[1], BothItems[0]);
            for(Node n : AllNodes)
                for (Room m : n.getRooms()) {
                    if(r.equals(m)){
                        return n.get_id();
                    }
                }
        }else {
            for(Node n : AllNodes)
                for (Room m : n.getRooms()) {
                    if(m.GetRoomNum().equals(BothItems[0]) || m.GetRoomName().equals(BothItems[0])){
                        return n.get_id();
                    }
                }
        }
        return null;
    }


    public void initDatBase(Context context){
        db = new DataBase(context);
    }

    public void closeDatabase(){
        if(db != null)
            db.close();
    }

    public Node getNodeByBeaconID(BeaconID bid) {
        for (Node node : AllNodes)
            if (bid.equals(node.get_id()))
                return node;

        return null;

    }

    public Bitmap getImageForPair(BeaconID id, BeaconID id2) {
        Bitmap img = db.getNodeImage(id.toString(), id2.toString());

        return img;
    }

    public void InitializeData(){
        db.getNodes(AllNodes);
    }

   /* public boolean insertImageToDB(BeaconID currentBeacon,int num, int dir, Bitmap res) {
        return db.insertImage(currentBeacon, num, dir, res);
    }*/

    public boolean insertNode(Node n) {
        if(db.insertNode(Node.getContentValues(n)))
           return AllNodes.add(n);
        return false;
    }

    public boolean insertNeighbourToNode(String string, String string1, int dir) {
        Node n1 = getNodeByBeaconID(BeaconID.from(string));
        Node n2 = getNodeByBeaconID(BeaconID.from(string1));

        if(db.insertRelation(string, string1, dir, false)){
           // int d = (dir + 180) % 360;
            return n1.AddNeighbour(new Pair<Node, Integer>(n2, dir));
           // n2.AddNeighbour(new Pair<Node, Integer>(n1, d));
        }
        return false;
    }

    public void insertRoomToNode(Room r, Node n) {
        if(db.insertRoom(n.get_id().toString(), r.GetRoomName(), r.GetRoomNum())){
            n.AddRoom(r);
        }

    }

    public void updateImage(BeaconID id, BeaconID id2, Bitmap res) {
        db.updateImage(id, id2, res);
    }


    public void clearData() {
        AllNodes.clear();
        db.clearDB();
    }

}
