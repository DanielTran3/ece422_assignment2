#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "Insertionsort.h"

void decrypt(jint*, jint*);

JNIEXPORT void JNICALL Java_TEA_decryption
  (JNIEnv *, jobject object, jintArray v, jintArray k) {
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
	array_copy = (jint *) (*env)->GetIntArrayElements(env, array_to_sort, is_copy);
    array_copy = (jint *) (*env)->GetIntArrayElements(env, , is_copy);
	if (array_copy == NULL){
	    printf("Cannot obtain array from JVM\n");
	    exit(0);
	}
}

void decrypt(jint* v, jint* k) {
    unsigned int n=32, sum, y=v[0], z=v[1];
    unsigned int delta=0x9e3779b9l;

    	sum = delta<<5;
    	while (n-- > 0){
    		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
    		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
    		sum -= delta;
    	}
    	v[0] = y;
    	v[1] = z;
    }
}
