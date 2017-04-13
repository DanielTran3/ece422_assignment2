#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "TEA.h"

void encrypt(jint*, jint*);

JNIEXPORT void JNICALL Java_TEA_encryption
  (JNIEnv *env, jobject object, jintArray v, jintArray k) {
    jsize len_v;
    jsize len_k;
    jint *v_copy;
    jint *k_copy;
    jboolean *is_copy = 0;
    jint sortPass;

    // Get the length of the array
    len_v = (*env)->GetArrayLength(env, v);
    len_k = (*env)->GetArrayLength(env, k);

    // Get a pointer to the array
    v_copy = (jint *) (*env)->GetIntArrayElements(env, v, is_copy);
    k_copy = (jint *) (*env)->GetIntArrayElements(env, k, is_copy);
    if ((v_copy == NULL) || (k_copy == NULL)){
      printf("Cannot obtain array from JVM\n");
      exit(0);
    }

    // Run the encryption
	int i;
	for (i = 0; i < len_v - 1; i++) {
		encrypt(v_copy + i, k_copy);
	}
    // Pass by reference of modified encryption to input array
    (*env)->SetIntArrayRegion(env, v, 0, len_v, v_copy);
}

// Encrytion algorithm
void encrypt(jint* v, jint* k) {
	unsigned int y = v[0], z=v[1], sum = 0;
	unsigned int delta = 0x9e3779b9, n=32;

	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}
