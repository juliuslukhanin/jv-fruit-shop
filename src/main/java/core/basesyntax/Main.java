package core.basesyntax;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.model.FruitShopTransactions;
import core.basesyntax.service.FileReader;
import core.basesyntax.service.FileWriter;
import core.basesyntax.service.FruitParser;
import core.basesyntax.service.OperationMap;
import core.basesyntax.service.OperationService;
import core.basesyntax.service.ReportCreator;
import core.basesyntax.service.impl.FileReaderImpl;
import core.basesyntax.service.impl.FileWriterImpl;
import core.basesyntax.service.impl.FruitParserImpl;
import core.basesyntax.service.impl.OperationMapImpl;
import core.basesyntax.service.impl.OperationServiceImpl;
import core.basesyntax.service.impl.ReportCreatorImpl;
import core.basesyntax.strategy.OperationHandler;
import core.basesyntax.strategy.impl.BalanceHandler;
import core.basesyntax.strategy.impl.PurchaseHandler;
import core.basesyntax.strategy.impl.ReturnHandler;
import core.basesyntax.strategy.impl.SupplyHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        StorageDao storageDao = new StorageDaoImpl();
        Map<FruitShopTransactions.Operation, OperationHandler> strategyMap = new HashMap<>();
        strategyMap.put(FruitShopTransactions.Operation.BALANCE, new BalanceHandler(storageDao));
        strategyMap.put(FruitShopTransactions.Operation.SUPPLY, new SupplyHandler(storageDao));
        strategyMap.put(FruitShopTransactions.Operation.PURCHASE, new PurchaseHandler(storageDao));
        strategyMap.put(FruitShopTransactions.Operation.RETURN, new ReturnHandler(storageDao));
        OperationMap operationMap = new OperationMapImpl(strategyMap);
        FileReader reader = new FileReaderImpl();
        List<String> input = reader.readFromFile("src/main/resources/inputFile.csv");
        FruitParser parser = new FruitParserImpl();
        List<FruitShopTransactions> fruitShopTransactions = parser.parse(input);
        OperationService operationService = new OperationServiceImpl(operationMap);
        operationService.processData(fruitShopTransactions);
        ReportCreator reportCreator = new ReportCreatorImpl(storageDao);
        String report = reportCreator.createReport();
        FileWriter writer = new FileWriterImpl();
        writer.writerDataToFile(report, "src/main/resources/reportFile.csv");
    }
}