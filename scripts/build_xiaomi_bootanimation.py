#!/usr/bin/env python3
import os
import math
import struct
import zlib
import zipfile

def create_png(width, height, draw_func):
    """Generates an uncompressed/zlib PNG using Python standard library."""
    raw_data = bytearray()
    for y in range(height):
        raw_data.append(0)  # Filter type None
        for x in range(width):
            r, g, b = draw_func(x, y, width, height)
            raw_data.extend((r, g, b))

    # Compress IDAT
    compressed = zlib.compress(bytes(raw_data), 6)

    def chunk(chunk_type, data):
        length = struct.pack('>I', len(data))
        crc = struct.pack('>I', zlib.crc32(chunk_type + data) & 0xffffffff)
        return length + chunk_type + data + crc

    # PNG Signature
    png = b'\x89PNG\r\n\x1a\n'
    # IHDR
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)
    png += chunk(b'IHDR', ihdr)
    # IDAT
    png += chunk(b'IDAT', compressed)
    # IEND
    png += chunk(b'IEND', b'')
    return png

def render_frame(x, y, w, h, angle_rad, pulse_factor, text_alpha):
    # Deep obsidian background
    bg_r, bg_g, bg_b = 4, 7, 13
    
    cx, cy = w / 2.0, h / 2.0
    dx = x - cx
    dy = y - cy
    dist = math.sqrt(dx * dx + dy * dy)
    
    ring_radius = w * 0.28
    ring_thickness = 4.0
    
    # Distance to ring
    diff = abs(dist - ring_radius)
    
    # Base cyan color (56, 189, 248)
    cyan_r, cyan_g, cyan_b = 56, 189, 248
    
    if diff < ring_thickness:
        # Ring intensity
        t = 1.0 - (diff / ring_thickness)
        
        # Rotating gradient along the ring
        point_angle = math.atan2(dy, dx) - angle_rad
        norm_angle = (point_angle % (2 * math.pi)) / (2 * math.pi)
        glow = (0.2 + 0.8 * norm_angle) * pulse_factor
        
        intensity = t * glow
        r = int(bg_r * (1 - intensity) + cyan_r * intensity)
        g = int(bg_g * (1 - intensity) + cyan_g * intensity)
        b = int(bg_b * (1 - intensity) + cyan_b * intensity)
        return min(255, max(0, r)), min(255, max(0, g)), min(255, max(0, b))
    elif diff < ring_thickness * 3.5:
        # Soft outer halo
        t = (1.0 - (diff / (ring_thickness * 3.5))) * 0.25 * pulse_factor
        r = int(bg_r * (1 - t) + cyan_r * t)
        g = int(bg_g * (1 - t) + cyan_g * t)
        b = int(bg_b * (1 - t) + cyan_b * t)
        return min(255, max(0, r)), min(255, max(0, g)), min(255, max(0, b))
    else:
        return bg_r, bg_g, bg_b

def main():
    base_dir = "xiaomi_theme"
    build_dir = os.path.join(base_dir, "build")
    part0_dir = os.path.join(build_dir, "part0")
    part1_dir = os.path.join(build_dir, "part1")
    preview_dir = os.path.join(base_dir, "preview")
    
    os.makedirs(part0_dir, exist_ok=True)
    os.makedirs(part1_dir, exist_ok=True)
    os.makedirs(preview_dir, exist_ok=True)
    
    w, h = 540, 1200  # Proportional 20:9 aspect ratio matching 1080x2400
    
    print("Generating part0 intro sequence (30 frames)...")
    for i in range(30):
        angle = (i / 30.0) * 2 * math.pi
        pulse = 0.5 + 0.5 * (i / 30.0)
        png_data = create_png(w, h, lambda x, y, width, height: render_frame(x, y, width, height, angle, pulse, 1.0))
        filename = os.path.join(part0_dir, f"{i:05d}.png")
        with open(filename, "wb") as f:
            f.write(png_data)
            
    print("Generating part1 ambient loop sequence (15 frames)...")
    for i in range(15):
        angle = (i / 15.0) * 2 * math.pi
        pulse = 0.85 + 0.15 * math.sin((i / 15.0) * 2 * math.pi)
        png_data = create_png(w, h, lambda x, y, width, height: render_frame(x, y, width, height, angle, pulse, 1.0))
        filename = os.path.join(part1_dir, f"{i:05d}.png")
        with open(filename, "wb") as f:
            f.write(png_data)
            
    # Copy preview frame
    preview_file = os.path.join(preview_dir, "preview_animation_0.png")
    with open(os.path.join(part0_dir, "00020.png"), "rb") as src, open(preview_file, "wb") as dst:
        dst.write(src.read())

    # Write desc.txt (strict UNIX newline)
    desc_path = os.path.join(build_dir, "desc.txt")
    with open(desc_path, "wb") as f:
        f.write(b"1080 2400 30\np 1 0 part0\np 0 0 part1\n")
        
    print("Packing bootanimation.zip with STORE (0% compression)...")
    boot_zip_path = os.path.join(base_dir, "bootanimation.zip")
    with zipfile.ZipFile(boot_zip_path, 'w', compression=zipfile.ZIP_STORED) as zip_out:
        zip_out.write(desc_path, "desc.txt")
        for root, _, files in os.walk(part0_dir):
            for file in sorted(files):
                zip_out.write(os.path.join(root, file), os.path.join("part0", file))
        for root, _, files in os.walk(part1_dir):
            for file in sorted(files):
                zip_out.write(os.path.join(root, file), os.path.join("part1", file))

    # Also prepare uncompressed 'bootanimation' file for MTZ archive root
    mtz_boot_file = os.path.join(build_dir, "bootanimation")
    with open(boot_zip_path, "rb") as src, open(mtz_boot_file, "wb") as dst:
        dst.write(src.read())
        
    # Write description.xml for Xiaomi MIUI/HyperOS
    desc_xml_path = os.path.join(base_dir, "description.xml")
    desc_xml_content = """<?xml version="1.0" encoding="utf-8"?>
<theme>
    <title>Aura Celestial Boot</title>
    <author>Aura</author>
    <designer>Aura Team</designer>
    <version>1.0.0</version>
    <uiVersion>14</uiVersion>
    <description>Minimalist celestial cyan ring boot animation crafted for Redmi Note 11 Pro and Xiaomi MIUI/HyperOS devices.</description>
</theme>
"""
    with open(desc_xml_path, "w", encoding="utf-8") as f:
        f.write(desc_xml_content)

    print("Building Aura_Redmi_Note_11_Pro.mtz...")
    mtz_path = os.path.join(base_dir, "Aura_Redmi_Note_11_Pro.mtz")
    with zipfile.ZipFile(mtz_path, 'w', compression=zipfile.ZIP_DEFLATED) as mtz_out:
        mtz_out.write(desc_xml_path, "description.xml")
        mtz_out.write(mtz_boot_file, "bootanimation")
        mtz_out.write(preview_file, "preview/preview_animation_0.png")
        
    print(f"Successfully built: {mtz_path} and {boot_zip_path}")

if __name__ == "__main__":
    main()
