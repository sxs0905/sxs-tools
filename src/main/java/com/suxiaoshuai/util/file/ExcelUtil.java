package com.suxiaoshuai.util.file;

public class ExcelUtil {

    // public static <T> List<T> readSingleSheet(FileInputStream inputStream, Class<T> tClass, int sheetIndex) {
    //     Field[] fields = tClass.getDeclaredFields();
    //     Map<Integer, String> fieldNameMap = new HashMap<>();
    //     int rowNum = 0;
    //     Workbook workbook = null;
    //     try {
    //         workbook = new XSSFWorkbook(inputStream);
    //         Sheet sheet = workbook.getSheetAt(sheetIndex);
    //         Row firstRow = sheet.getRow(sheet.getFirstRowNum());
    //         short lastCellNum = firstRow.getLastCellNum();
    //         for (int i = 0; i < lastCellNum; i++) {
    //             Cell cell = firstRow.getCell(i);
    //             fieldNameMap.put(i, cell.toString());
    //         }
    //         int lastRowNum = sheet.getLastRowNum();
    //         List<T> list = new ArrayList<>(lastRowNum);
    //         for (int i = 1; i <= lastRowNum; i++) {
    //             rowNum = i;
    //             Row row = sheet.getRow(i);
    //             T t = tClass.newInstance();
    //             for (int j = 0; j < lastCellNum; j++) {
    //                 Cell cell = row.getCell(j);
    //                 String s = fieldNameMap.get(j);
    //                 for (int z = 0; z < fields.length; z++) {
    //                     Field field = fields[z];
    //                     if (field.getName().equalsIgnoreCase(s)) {
    //                         field.setAccessible(true);
    //                         field.set(t, cell.toString());
    //                         break;
    //                     }
    //                 }
    //
    //             }
    //             list.add(t);
    //         }
    //         return list;
    //     } catch (Exception e) {
    //         System.out.println(e);
    //         throw new FileException("9999", "第[" + rowNum + "]行数据解析失败" + e.getMessage());
    //     } finally {
    //         if (workbook != null) {
    //             try {
    //                 workbook.close();
    //             } catch (IOException e) {
    //                 System.out.println("111");
    //             }
    //         }
    //         if (inputStream != null) {
    //             try {
    //                 inputStream.close();
    //             } catch (IOException e) {
    //                 System.out.println("111");
    //             }
    //         }
    //     }
    // }
}
