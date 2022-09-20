package com.doudoudrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>兜兜网盘 - 文件系统配置中心</p>
 * <p>2022-04-07 19:45</p>
 *
 * @author Dan
 **/
@EnableWebMvc
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.doudoudrive.file.client"})
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);

//        List<String> list = new ArrayList<>();
//        for (int i = 1; i <= 10000; i++) {
//            list.add(SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE));
//        }
//
//        System.out.println(ObjectUtil.serialize(DeleteFileConsumerRequestDTO.builder()
//                .userId("22071702211816579956785050380374347")
//                .businessId(list)
//                .build()).length);

//        String url = "http://192.168.31.200:1075/file/create-folder";
//
//        // 构建回调地址初始化请求头配置Map
//        Map<String, String> header = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_THREE);
//        header.put(ConstantConfig.HttpRequest.TOKEN, "e4165a39-e809-4a76-8c1f-246be8f11f5f");
//
//        // 10000000
//        String s = "22091408291216631153522781165141570";
//        for (int i = 1; i <= 10000000; i++) {
//            //System.out.println(i);
//            String body = "{\"name\": \"测试文件夹" + i + "\",\"parentId\": \""+s+"\"}";
//            try (cn.hutool.http.HttpResponse execute = HttpRequest.post(url)
//                    .headerMap(header, Boolean.TRUE)
//                    .contentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON)
//                    .charset(StandardCharsets.UTF_8)
//                    .body(body.getBytes(StandardCharsets.UTF_8))
//                    .timeout(NumberConstant.INTEGER_THREE * NumberConstant.INTEGER_ONE_THOUSAND)
//                    .execute()) {
//                if (execute.getStatus()==200) {
//                    System.out.println(execute.body());
//                    JSONObject jsonObject = JSONObject.parseObject(execute.body());
//                    if (jsonObject.getInteger("code") == 200) {
//                        //s = jsonObject.getJSONObject("data").getString("businessId");
//                    }
//                } else {
//                    System.out.println(execute.toString());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

}
