import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 시작: 23:34
 * 완료:
 * 
 * 
 * 문제해석
 * LxL 크기의 체스판
 * (1,1)~()
 * 빈칸,함정,벽
 * 체스판밖==벽
 * 
 * 기사 체력k
 * 1. 기사이동
 * 상하좌우 1칸 이동
 * 이동 위치에 다른 기사 있으면, 그 기사도 함께 1칸 밀려남
 * 그 옆에 또 기사 있으면 1칸씩 연쇄적으로 또 밀려남
 * 그러나 기사가 이동하려는 방향의 끝에 벽이 있으면 모든 기사 이동x
 * 체스판에서 사라진 기사에게 명령을 내리면 반응x
 * 
 * 2.대결 데미지
 * 다른 기사 밀치면, 밀려난 기사들은 피해
 * 각 기사들은 해당 기사가 이동한 곳에서 wxh 내에 놓여있는 함정의 수만큼 피해
 * 각 기사마다 피해를 받은 만큼 체력 깎임
 * 현재 체력이상의 데미지 받으면 체스판에서 사라짐
 * (명령받은 기사는 피해x, 기사들은 모두 밀린 이후 피해입음)
 * 밀렸더라도 밀쳐진 위치에 함정이 없으면 그 기사는 피해를 안입음
 * 
 * 
 * 입력
 * 첫째줄: L격자크기,N기사수,Q명령수
 * L개줄: 격자정보 0빈칸 1함정 2벽
 * N개줄: 초기 기사정보 r,c,h,w,k
 * 				(r,c) 위치, (r,c기준 wxh직사각형), 초기체력k
 * Q개줄: 명령정보 i,d
 * 			i번기사게 d방향으로 한칸이동
 * (이미사라진 기사일 수도 있음)
 * d=0,1,2,3 상우하좌 순
 * 
 * 출력
 * Q번에 걸쳐 명령,  Q번의 대결이 모두 끝난 후 생존한 기사들이 총 받은 데미지의 합
 * 
 * 문제해결프로세스
 * 0. 기사 맵에 다놓음
 * 1. 해당기사와 번호가 같은 좌표 이동
 * 	다른 기사 만남 -> 그 기사 queue에 넣음(해당번호 추가)
 * 	범위밖 또는 벽 -> 이동불가 return false;
 * 	queue 안전하게 탈출 -> 이동가능 return true; + 이동한 기사들 시작점 1칸씩 이동
 * 2. 이동한 기사들 데미지 계산
 * 	데미지가 체력보다 크면 없앰
 * 
 * 3. 종료
 * 	남은 병사들 중 데미지의 합
 * 	
 *  
 * 시간복잡도
 * 
 * 
 */
public class Main {
	static int L,N,Q;
	static int[][] map;
	static List<Bot> list = new ArrayList<>();
	static int dx[] = {-1,0,1,0};
	static int dy[] = {0,1,0,-1};
	static class Bot {
		int num, r,c, h,w, k, init;

		public Bot(int num, int r, int c, int h, int w, int k, int init) {
			super();
			this.num = num;
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			this.init = init;
		}

		@Override
		public String toString() {
			return "Bot [num=" + num + ", r=" + r + ", c=" + c + ", h=" + h + ", w=" + w + ", k=" + k + ", init=" + init
					+ "]";
		}
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		map = new int[L][L];
		for (int i = 0; i < L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken())-1;
			int c = Integer.parseInt(st.nextToken())-1;
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			int init = 0;
			list.add(new Bot(i,r,c,h,w,k,init));
		}
		
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int n = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			move(n,d);
		}
		
		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			Bot b = list.get(i);
			result+=b.init;
		}
		System.out.println(result);
		
	}
	/* 문제해결프로세스
	 * 0. 기사 맵에 다놓음
	 * 1. 해당기사와 번호가 같은 좌표 이동
	 * 	다른 기사 만남 -> 그 기사 queue에 넣음(해당번호 추가)
	 * 	범위밖 또는 벽 -> 이동불가 return false;
	 * 	queue 안전하게 탈출 -> 이동가능 return true; + 이동한 기사들 시작점 1칸씩 이동
	 * 2. 이동한 기사들 데미지 계산
	 * 	데미지가 체력보다 크면 없앰
	 * 
	 * 3. 종료
	 * 	남은 병사들 중 데미지의 합
	 */
	public static int calculate(int r, int c, int h, int w, int d) {
		int cnt = 0;
		for (int a = r; a < r+h; a++) {
			for (int b = c; b < c+w; b++) {
				int nx = a+dx[d];
				int ny = b+dy[d];
				if(map[nx][ny]==1) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	
	public static void move(int n, int d) {
		int[][] copymap = new int[L][L];
		List<Integer> is_ok = new ArrayList<>();
		Queue<Bot> q = new ArrayDeque<>();
		boolean[] visited = new boolean[N+1]; 
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).num==n) {
				q.offer(list.get(i));
				visited[n] = true;
				break;
			}
		}
		
		for (int i = 0; i < list.size(); i++) {
			Bot bot = list.get(i);
			int num = bot.num;
			int r = bot.r;
			int c = bot.c;
			int h = bot.h;
			int w = bot.w;
			for (int a = r; a < r+h; a++) {
				for (int b = c; b < c+w; b++) {
					copymap[a][b] = num;
				}
			}
		}
		
		while(!q.isEmpty()) {
			int size = q.size();
			
			for (int i = 0; i < size; i++) {
				Bot bot = q.poll();
				int num = bot.num;
				int r = bot.r;
				int c = bot.c;
				int h = bot.h;
				int w = bot.w;
				for (int a = r; a < r+h; a++) {
					for (int b = c; b < c+w; b++) {
						int nx = a+dx[d];
						int ny = b+dy[d];
						if(!rangecheck(nx,ny) || map[nx][ny]==2) {
							return;
						}
						//else
						else if(copymap[nx][ny]>0 && !visited[copymap[nx][ny]]) {
							for (int cm = 0; cm < list.size(); cm++) {
								int bnum = list.get(cm).num;
								if(bnum==copymap[nx][ny]) {
									q.offer(list.get(cm));
									visited[copymap[nx][ny]] = true;
								}
							}
						}
					}
				}
				is_ok.add(num);
			}
		}
		
		int size = list.size();
		for (int i = size-1; i >=0; i--) {
			Bot bot = list.get(i);
			int num = bot.num;
			int r = bot.r;
			int c = bot.c;
			int h = bot.h;
			int w = bot.w;
			int k = bot.k;
			int init = bot.init;
			if(is_ok.contains(num)){
				if(n!=num) {
					init += calculate(r,c,h,w,d);
				}
				if(init>=k) {
					list.remove(i);
				}
				else {
					list.set(i, new Bot(num, r+dx[d],c+dy[d],h,w,k,init));
				}
			}
		}
		return;
	}
	
	public static boolean rangecheck(int rx, int ry) {
		return rx>=0 && rx<L && ry>=0 && ry<L;
	}

}