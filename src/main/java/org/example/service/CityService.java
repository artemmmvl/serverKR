package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.example.entity.City;
import org.example.repo.CityRepo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@Service
@AllArgsConstructor
public class CityService {
    private final CityRepo cityRepo;
    public void setCities(){
        List<City> cities=new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "GET", "https://api.hh.ru/areas");
            System.out.println(processBuilder.command());
            Process process = processBuilder.start();
            process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder line1 = new StringBuilder();
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                line1.append(line);
            }
            JSONParser jsonParser = new JSONParser();

            JSONArray items= (JSONArray) jsonParser.parse(line1.toString());
            for (var obj: items){
                JSONObject object= (JSONObject) obj;
                if (object.get("name").equals("Россия")){
                    JSONArray areas= (JSONArray) object.get("areas");
                    for (var area: areas) {
                        JSONObject objectArea= (JSONObject) area;
                        JSONArray areasChilds= (JSONArray) objectArea.get("areas");
                        for(var areaChild:areasChilds){
                            JSONObject objectAreaChild= (JSONObject) areaChild;
                            City city=new City(Long.parseLong( objectAreaChild.get("id").toString()), objectAreaChild.get("name").toString());
                            cities.add(city);
//                            System.out.println(objectAreaChild.get("name"));
                        }

                    }

                }
            }
            cities.add(new City(1L, "Москва"));
            cities.add(new City(2L, "Санкт-Петербург"));
            cityRepo.saveAll(cities);
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<City> getAll(){
        return cityRepo.findAll();
    }
    public City getById(Long id){
        return cityRepo.getReferenceById(id);
    }

}
