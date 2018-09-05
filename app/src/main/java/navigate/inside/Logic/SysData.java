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
        for(Node n : AllNodes)
            for (Room m : n.getRooms()) {
                if(room.equals(m.GetRoomName()) || room.equals(m.GetRoomNum())){
                    return n.get_id();
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

    public Bitmap getImageForNode(BeaconID id) {
        Bitmap img = db.getNodeImage(id.toString());

        return img;
    }

    public void InitializeData(){
        db.getNodes(AllNodes);
    }
    public void insertImageToDB(BeaconID currentBeacon, Bitmap res) {
        db.insertImage(currentBeacon, res);
    }

    public void insertNode(Node n) {
        if(db.insertNode(Node.getContentValues(n)))
            AllNodes.add(n);

    }

    public void insertNeighbourToNode(String string, String string1, int dir) {
        Node n1 = getNodeByBeaconID(BeaconID.from(string));
        Node n2 = getNodeByBeaconID(BeaconID.from(string1));

        if(db.insertRelation(string, string1, dir, false)){
            int d = (dir + 180) % 360;
            n1.AddNeighbour(new Pair<Node, Integer>(n2, dir));
            n2.AddNeighbour(new Pair<Node, Integer>(n1, d));
        }

    }
}
