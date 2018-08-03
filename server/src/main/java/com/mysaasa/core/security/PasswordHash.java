package com.mysaasa.core.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/*
 * PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * www: http://crackstation.net/hashing-security.htm
 * 
 * Note: Code was in public domain, so inhereted. #Adam
 */
public class PasswordHash {
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	// The following constants may be changed without breaking existing hashes.
	private static final int SALT_BYTE_SIZE = 24;
	private static final int HASH_BYTE_SIZE = 24;
	private static final int PBKDF2_ITERATIONS = 1000;

	private static final int ITERATION_INDEX = 0;
	private static final int SALT_INDEX = 1;
	private static final int PBKDF2_INDEX = 2;

	/**
	 * Returns a salted PBKDF2 hash of the password.
	 *
	 * @param password the password to hash
	 * @return a salted PBKDF2 hash of the password
	 */
	private static String createHash(char[] password) {
		// Generate a random salt
		final SecureRandom random = new SecureRandom();
		final byte[] salt = new byte[SALT_BYTE_SIZE];
		random.nextBytes(salt);

		// Hash the password
		final byte[] hash;
		try {
			hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
			return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		// format iterations:salt:hash
		throw new RuntimeException("Could not hash");
	}

	/**
	 * Returns a salted PBKDF2 hash of the password.
	 *
	 * @param password the password to hash
	 * @return a salted PBKDF2 hash of the password
	 */
	public static String createHash(String password) {
		return createHash(password.toCharArray());
	}

	/**
	 * Converts a string of hexadecimal characters into a byte array.
	 *
	 * @param hex the hex string
	 * @return the hex string decoded into a byte array
	 */
	private static byte[] fromHex(String hex) {
		final byte[] binary = new byte[hex.length() / 2];
		for (int i = 0; i < binary.length; i++) {
			binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return binary;
	}

	/**
	 * Tests the services functionality of the PasswordHash class
	 *
	 * @param args ignored
	 */
	public static void main(String[] args) {
		try {
			// Print out 10 hashes
			for (int i = 0; i < 10; i++) {
				//(PasswordHash.createHash("p\r\nassw0Rd!"));
			}

			// Test password validation
			boolean failure = false;
			//("Running tests...");
			for (int i = 0; i < 100; i++) {
				final String password = "" + i;
				final String hash = createHash(password);
				final String secondHash = createHash(password);
				if (hash.equals(secondHash)) {
					//("FAILURE: TWO HASHES ARE EQUAL!");
					failure = true;
				}
				final String wrongPassword = "" + (i + 1);
				if (validatePassword(wrongPassword, hash)) {
					//("FAILURE: WRONG PASSWORD ACCEPTED!");
					failure = true;
				}
				if (!validatePassword(password, hash)) {
					//("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
					failure = true;
				}
			}
			if (failure) {
				//("TESTS FAILED!");
			} else {
				//("TESTS PASSED!");
			}
		} catch (final Exception ex) {
			//("ERROR: " + ex);
		}
	}

	/**
	 * Computes the PBKDF2 hash of a password.
	 *
	 * @param password   the password to hash.
	 * @param salt       the salt
	 * @param iterations the iteration count (slowness factor)
	 * @param bytes      the length of the hash to compute in bytes
	 * @return the PBDKF2 hash of the password
	 */
	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
		final SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		return skf.generateSecret(spec).getEncoded();
	}

	/**
	 * Compares two byte arrays in length-constant time. This comparison method is
	 * used so that password hashes cannot be extracted from an on-line system
	 * using a timing attack and then attacked off-line.
	 *
	 * @param a the first byte array
	 * @param b the second byte array
	 * @return true if both byte arrays are the same, false if not
	 */
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * Converts a byte array into a hexadecimal string.
	 *
	 * @param array the byte array to convert
	 * @return a length*2 character string encoding the byte array
	 */
	private static String toHex(byte[] array) {
		final BigInteger bi = new BigInteger(1, array);
		final String hex = bi.toString(16);
		final int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0)
			return String.format("%0" + paddingLength + "d", 0) + hex;
		else
			return hex;
	}

	/**
	 * Validates a password using a hash.
	 *
	 * @param password    the password to check
	 * @param correctHash the hash of the valid password
	 * @return true if the password is correct, false if not
	 * @throws  NoSuchAlgorithmException no algorithm
	 * @throws InvalidKeySpecException invalid key
	 */
	private static boolean validatePassword(char[] password, String correctHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Decode the hash into its parameters
		final String[] params = correctHash.split(":");
		final int iterations = Integer.parseInt(params[ITERATION_INDEX]);
		final byte[] salt = fromHex(params[SALT_INDEX]);
		final byte[] hash = fromHex(params[PBKDF2_INDEX]);
		// Compute the hash of the provided password, using the same salt,
		// iteration count, and hash length
		final byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
		// Compare the hashes in constant time. The password is correct if
		// both hashes match.
		return slowEquals(hash, testHash);
	}

	/**
	 * Validates a password using a hash.
	 *
	 * @param password    the password to check
	 * @param correctHash the hash of the valid password
	 * @return true if the password is correct, false if not
	 * @throws  NoSuchAlgorithmException no algorithm
	 * @throws InvalidKeySpecException invalid key
	 */
	public static boolean validatePassword(String password, String correctHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (password == null)
			return false;
		return validatePassword(password.toCharArray(), correctHash);
	}

}
