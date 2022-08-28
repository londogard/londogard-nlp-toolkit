import gzip
from pathlib import Path
import msgpack
import glob

root_path = glob.glob("*.gzip")

print(root_path)

#with gzip.open(data_path, 'rb') as infile:
##    data = msgpack.load(infile, raw=False)
 #   header = data[0]

#print(data[:1])

