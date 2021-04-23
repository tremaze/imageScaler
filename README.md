# Image Scaler
This library can be used to scale images to fixed sizes. In the current version JPGs and PNGs can be scaled. The default scaling sizes are 100, 200, 400, 600, 800, 1000, 1300, 1600, 1920 but they can be overwritten.

## How to use
```java
File file = new File("/path/to/original/file/dir");
Compressor compressor = new Compressor(file.toPath(), "testfile.jpg");
try {
  List<ScaledImage> images = compressor.compress();
} catch (ImageTypeNotAllowedException e) {
  e.printStackTrace();
}
```
## Tests
There are some testcases for the most common use cases. You can run tests for the following cases:
- jpg
- png
- png transparent
- no scaling needed
- not allowed image type

## License
This code is under the [MIT License](LICENSE).
