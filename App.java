// package meet_in_the_middle_attack_java;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class App {
    public static void main(String[] args) {

        BigInteger b = new BigInteger("2").pow(20);
        BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084171");
        BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979459063929499736583746670572176471460312928594829675428279466566527115212748467589894601965568");
        BigInteger h = new BigInteger("3239475104050450443565264378728065788649097520952449527834792452971981976143292558073856937958553180532878928001494706097394108577585732452307673444020333");
        BigInteger x = new BigInteger("0");

        long start = System.currentTimeMillis();
        Map<BigInteger, BigInteger> map = new ConcurrentHashMap<>();
        Set<BigInteger> set = new HashSet<>();

        for(BigInteger i = BigInteger.ZERO; i.compareTo(b) < 0; i = i.add(BigInteger.ONE)) {
            set.add(i);
        }

        set.parallelStream().forEach(i -> {
            BigInteger g_pow_i = g.modPow(i, p);
            BigInteger h_div_g_pow_i = h.multiply(g_pow_i.modInverse(p)).mod(p);
            map.put(h_div_g_pow_i, i);
        });
        
        BigInteger g_pow_b = g.modPow(b, p);
        AtomicReference<BigInteger> atomic_x = new AtomicReference<>(BigInteger.ZERO);
        set.parallelStream().forEach(i -> {
            if(atomic_x.get().compareTo(BigInteger.ZERO) != 0) {
                return;
            }
            BigInteger g_pow_b_pow_i = g_pow_b.modPow(i, p);
            if(map.containsKey(g_pow_b_pow_i)) {
                BigInteger x1 = map.get(g_pow_b_pow_i);
                BigInteger x0 = i;
                atomic_x.set(x0.multiply(b).add(x1));
            }
        });

        long end = System.currentTimeMillis();

        System.out.printf("x = %s\n", atomic_x.get());
        BigInteger res = g.modPow(atomic_x.get(), p);
        System.out.printf("g^x mod p = %s \n", res);
        System.out.printf("h = g^x mod p ? %s \n", h.compareTo(res) == 0 ? "Yes" : "No");
        System.out.printf("Time: %s ms\n", end - start);  

    }
}
