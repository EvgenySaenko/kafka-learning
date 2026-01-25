## Как поднять кафку

### Запустить
```bash
docker compose -f .\docker\kafka\docker-compose.yml up -d
```

### Остановить
```bash
docker compose -f .\docker\kafka\docker-compose.yml down
```


### Открой UI в браузере

Скорее всего у тебя такие порты (если ты их так прописал в compose):

+ Kafdrop: http://localhost:9000
+ AKHQ: http://localhost:8088
+ Redpanda Console: http://localhost:8089