/*
 * Minio Java Example, (C) 2016 Minio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minio.photoapiservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import org.xmlpull.v1.XmlPullParserException;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.errors.MinioException;
import io.minio.http.Method;

public class AlbumDao {
    public List<Album> listAlbums() throws NoSuchAlgorithmException,
            IOException, InvalidKeyException, XmlPullParserException, MinioException {

        List<Album> list = new ArrayList<Album>();
        final String minioBucket = "albums";

        MinioClient minioClient =
        	    MinioClient.builder()
        	        .endpoint("https://play.min.io")
        	        .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
        	        .build();

        // List all objects.
        Iterable<Result<Item>> myObjects = minioClient.listObjects(
        	    ListObjectsArgs.builder().bucket(minioBucket).build());

        // Iterate over each elements and set album url.
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());

            // Generate a presigned URL which expires in a day
            String url =
            	    minioClient.getPresignedObjectUrl(
            	        GetPresignedObjectUrlArgs.builder()
            	            .method(Method.GET)
            	            .bucket(minioBucket)
            	            .object(item.objectName())
            	            .expiry(24, TimeUnit.HOURS)
            	            .build()); 
             
            // Create a new Album Object
            Album album = new Album();
            
            // Set the presigned URL in the album object
            album.setUrl(url);
            
            // Add the album object to the list holding Album objects
            list.add(album);
            
        }

        // Return list of albums.
        return list;
    }
}
