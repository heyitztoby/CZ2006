package com.example.parkez.Model;

import java.util.ArrayList;

public class CarparkAvailability {

    private ArrayList<Main> items;

    public ArrayList<Main> getItems() {
        return items;
    }

    public class Main {
        private String timestamp;
        private ArrayList<CarparkData> carpark_data;

        public String getTimestamp() {
            return timestamp;
        }

        public ArrayList<CarparkData> getCarpark_data() {
            return carpark_data;
        }
    }

    public class CarparkData {
        private String carpark_number, update_datetime;
        private ArrayList<CarparkInfo> carpark_info;

        public String getCarpark_number() {
            return carpark_number;
        }

        public String getUpdate_datetime() {
            return update_datetime;
        }

        public ArrayList<CarparkInfo> getCarpark_info() {
            return carpark_info;
        }
    }

    public class CarparkInfo {
        private String total_lots, lot_type, lots_available;

        public String getTotal_lots() {
            return total_lots;
        }

        public String getLot_type() {
            return lot_type;
        }

        public String getLots_available() {
            return lots_available;
        }
    }
}
