import java.util.List;

public class Test {
    public static void main(String[] args) {
        Graph campusMap = new Graph();

        // 1. 初始化地点详情
        campusMap.addLocation("Main Gate", new Location("Entrance", "Security"));
        campusMap.addLocation("Library", new Location("Academic", "Zone A"));
        campusMap.addLocation("Fakulti Sains", new Location("Faculty", "Main Building"));
        campusMap.addLocation("Kolej Makanan", new Location("Dining", "Block C"));
        campusMap.addLocation("Dorm A", new Location("Hostel", "Block A"));

        // 2. 建立路线
        campusMap.addEdge("Main Gate", "Library", 500);
        campusMap.addEdge("Main Gate", "Fakulti Sains", 800);
        campusMap.addEdge("Library", "Fakulti Sains", 200);
        campusMap.addEdge("Library", "Kolej Makanan", 600);
        campusMap.addEdge("Fakulti Sains", "Kolej Makanan", 300);
        campusMap.addEdge("Kolej Makanan", "Dorm A", 400);

        // 3. 展示地图
        campusMap.displayGraph();

        // 4. 测试派送路径：骑手从正门出发，去食堂取餐，送往宿舍A
        System.out.println("\n--- 派送任务测试 ---");
        List<String> route = campusMap.getDeliveryRoute("Main Gate", "Kolej Makanan", "Dorm A");
        
        if (route.isEmpty()) {
            System.out.println("路径不可达！");
        } else {
            System.out.println("派送完整路径: " + String.join(" -> ", route));
        }
    }
}