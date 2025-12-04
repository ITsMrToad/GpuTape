# GPUBooster | About⚡
![fo](https://cdn.modrinth.com/data/cached_images/8457cf2b01e18302ef61da3256328bfb4b94ad14.png)
![fa](https://cdn.modrinth.com/data/cached_images/6c152554bb3141433b6dca087131f96f00a876ba.png)
![qu](https://cdn.modrinth.com/data/cached_images/554809ee9ae740db0707b7cd1420d937b490cd6b.png)
![neo](https://cdn.modrinth.com/data/cached_images/5d6378864df2d8f2699289ec7f439e26b5c8db60.png)
</br>
![i](https://cdn.modrinth.com/data/cached_images/ca0de18375553419eff9ce315dccceb885971010.png)
<br />

### Features ⭐:
- Fast math
- Fast random
- DSA system for VBO/EBO/FBO
- RBO depth 
- Pool system for VBO/EBO
- "VAO <--> Format" cache
- Bindless textures(DSA format)

### DSA:
DSA(direct-state access) buffers(VBO, EBO, FBO, Textures), allows you not to bind buffers to render, reducing the number of operations, also adds a VBO/EBO pool, which creates a config-defined size value and allows you to reuse buffers.

### RBO:
Uses a Renderbuffer(RBO) for depth (which also uses DSA if it enabled on "FBO only" or "All") instead of texture, this slightly increases performance.

# GPUBooster | User-side 👤

### Problem-solving policy:
If issue marked as “Bug/Compat,” it will have the highest priority. 
<br/>
If issue marked as “Inaccuracy,” it has a low priority and implies that there is minor inaccuracy in the game process, which can be resolved by disabling config setting.
<br/>
Create an issue using the provided templates

### Important ⚠️:
If your GPU's OpenGL is lower than 4.5, some features will be unavailable!
RBO not compatible with shaderpacks.
Since minecraft 1.21.10 GpuTape support will be discontinued(Use GPUBooster instead). 

### Available languanges 🌐:
- English(en_us) - Mr.Toad
- Russian(ru_ru) - Mr.Toad
- German(de_de) - Mr.Toad (<1.1)
- Ukrainian(uk_ua) - StarmanMine142(<1.1)

### Other my mods:
- [H+](https://modrinth.com/mod/h_plus)(New content & Difficulty Improvements)
- [EnlightenedBlockEntities/EBEReforged](https://modrinth.com/mod/ebe-forge)(Fork of enchanced block entities)[DISCONTINUED]
- [Palladium](https://modrinth.com/mod/mpalladium)(Optimization)
- [MovieMaker](https://modrinth.com/mod/moviemaker)(Content)
