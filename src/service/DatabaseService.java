package service;

import model.Query;

public class DatabaseService {

    public static void handleQuery(Query query) {
        System.out.println("DatabaseService: handled Query - " + query.getMessage());
    }
}
