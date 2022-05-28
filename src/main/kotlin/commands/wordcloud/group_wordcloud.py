import sys
import jieba
from wordcloud import WordCloud

if __name__ == "__main__":
    group = sys.argv[1]
    with open(f"../../../../../data/{group}", "r", encoding="utf-8") as f:
        text = f.read()
    with open("./stopwords_list.txt", "r", encoding="utf-8") as f:
        stopwords = f.read().split("\n")
    new_text = " ".join(jieba.cut(text))
    wc = WordCloud(font_path="STCAIYUN.TTF", background_color="white", width=800, height=400, stopwords=set(stopwords))\
        .generate(new_text)
    wc.to_file(f"../../../../../data/{group}.png")
