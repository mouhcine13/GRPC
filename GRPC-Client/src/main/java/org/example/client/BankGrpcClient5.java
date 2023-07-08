package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.stubs.Bank;
import org.example.stubs.BankServiceGrpc;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class BankGrpcClient5 {
    public static void main(String[] args) throws IOException {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",5555)
                .usePlaintext()
                .build();
        BankServiceGrpc.BankServiceStub asyncStub = BankServiceGrpc.newStub(managedChannel);
        Bank.ConvertCurrencyRequest request = Bank.ConvertCurrencyRequest.newBuilder()
                .setCurrencyFrom("MAD")
                .setCurrencyTo("EUR")
                .setAmmount(6500)
                .build();
        StreamObserver<Bank.ConvertCurrencyRequest> streamObserver =
                asyncStub.fullCurrencyStream(new StreamObserver<Bank.ConvertCurrencyResponse>() {
            @Override
            public void onNext(Bank.ConvertCurrencyResponse convertCurrencyResponse) {
                System.out.println("==========================");
                System.out.println(convertCurrencyResponse);
                System.out.println("==============================");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("END");
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                Bank.ConvertCurrencyRequest currencyRequest = Bank.ConvertCurrencyRequest.newBuilder()
                                .setAmmount((float) (Math.random()*7000))
                                        .build();
                streamObserver.onNext(currencyRequest);
                System.out.println("Counter: "+counter);
                ++counter;
                if (counter == 20){
                    streamObserver.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
        System.out.println("Fin du code ?");
        System.in.read();
    }
}
